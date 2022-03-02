package cn.flandre.json.socket.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static cn.flandre.json.constant.IOConstant.BLOCK_SIZE;

/**
 * 读数据时先从缓冲区读入
 * 当缓冲区为空时再从网络读数据
 */
public class SocksInputStream extends InputStream {
    private final SocketChannel sc;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(BLOCK_SIZE);

    public SocksInputStream(SocketChannel sc) {
        this.sc = sc;
        byteBuffer.flip();
    }

    public void clear() {
        byteBuffer.position(0);
        byteBuffer.limit(0);
    }

    @Override
    public int read() throws IOException {
        if (byteBuffer.remaining() == 0) {  // 没有空间了先读取空间
            byteBuffer.clear();
            int len = sc.read(byteBuffer);
            if (len <= 0) {
                return -1;
            }
            byteBuffer.flip();
        }
        return (int) byteBuffer.get() & 0xff;  // 返回缓冲的一个字节
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int offset = 0, r, min;
        if (byteBuffer.remaining() > 0) {  // 缓冲还有数据先从缓冲读
            min = Math.min(len, byteBuffer.remaining());
            byteBuffer.get(b, off, min);
            if (byteBuffer.remaining() > 0)
                return min;
            offset = min;
        }
        while (offset < len) {  // 若还需要读数据
            byteBuffer.clear();
            r = sc.read(byteBuffer);
            byteBuffer.flip();
            if (r <= 0) {  // EOF
                break;
            }
            min = Math.min(len - offset, byteBuffer.remaining());
            byteBuffer.get(b, offset + off, min);
            offset += min;
        }
        return offset == 0 ? -1 : offset;
    }

    public boolean require(int len) throws IOException {
        if (byteBuffer.remaining() >= len) return true;
        if (byteBuffer.remaining() == 0) byteBuffer.clear();
        if (byteBuffer.position() > 0) byteBuffer.compact();
        sc.read(byteBuffer);
        byteBuffer.flip();
        return byteBuffer.remaining() >= len;
    }

    @Override
    public String toString() {
        return "SocksInputStream{" +
                "extra=" + byteBuffer +
                '}';
    }
}
