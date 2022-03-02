package cn.flandre.json.socket.stream;

import cn.flandre.json.socket.exception.SystemBufferOverflowException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static cn.flandre.json.constant.IOConstant.BLOCK_SIZE;

/**
 * 缓冲要发送的数据
 * 缓冲区满了就发送
 */
public class SocksOutputStream extends OutputStream {
    private final SocketChannel sc;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(BLOCK_SIZE);

    public SocksOutputStream(SocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void write(int b) throws IOException {
        if (!byteBuffer.hasRemaining()) {
            flush();
        }
        byteBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int offset = 0, min;
        while (offset < len) {
            if (!byteBuffer.hasRemaining()) {  // 没有空间时刷新缓冲区
                try {
                    flush();
                } catch (SystemBufferOverflowException e) {
                    e.setLen(offset);
                    throw e;
                }
            } else {  // 把缓冲区写满
                min = Math.min(len - offset, byteBuffer.remaining());
                byteBuffer.put(b, offset + off, min);
                offset += min;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        int should, write, p, l;
        p = byteBuffer.position();  // 记录状态
        l = byteBuffer.limit();
        byteBuffer.flip();
        should = byteBuffer.remaining();
        write = sc.write(byteBuffer);
        if (write != should) {  // 当系统缓冲区满时
            if (write > 0) {
                byteBuffer.compact();
            } else {  // 回滚状态
                byteBuffer.position(p);
                byteBuffer.limit(l);
            }
            throw new SystemBufferOverflowException(0);
        }
        byteBuffer.clear();
    }

    @Override
    public String toString() {
        return "SocksOutputStream{" +
                "bytes=" + byteBuffer +
                '}';
    }
}