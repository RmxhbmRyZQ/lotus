package cn.flandre.lotus.socket.stream;

import cn.flandre.lotus.exception.SystemBufferOverflowException;
import cn.flandre.lotus.http.match.WriteFinish;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class BlockOutputStream extends OutputStream {
    private final FreeBlock freeBlock;
    private final LinkedList<Block> queue;
    private final SocksOutputStream os;
    private Block buffer;
    private WriteFinish writeFinish;
    private long len;

    public BlockOutputStream(SocksOutputStream sos, FreeBlock freeBlock) {
        this.freeBlock = freeBlock;
        queue = new LinkedList<>();
        os = sos;
        buffer = freeBlock.poll();
        queue.add(buffer);
    }

    public boolean writeFully() throws IOException {
        if (queue.getFirst().isEmpty()) {
            try {
                os.flush();
                if (writeFinish != null)  // 写完成，回调
                    writeFinish.writeFinish();
                return true;
            } catch (SystemBufferOverflowException e) {
                return false;
            }
        }

        int w;
        while (true) {
            Block block = queue.poll();  // 从写入队列取出块来写
            w = block.write(os);
            len -= w;
            block.incPos(w);
            if (block.isEmpty()) {
                if (queue.size() != 0)
                    freeBlock.add(block);
                else {
                    block.reset();
                    queue.add(block);
                    try {
                        os.flush();
                        if (writeFinish != null)  // 写完成，回调
                            writeFinish.writeFinish();
                        return true;
                    } catch (SystemBufferOverflowException e) {
                        return false;
                    }
                }
            } else {
                queue.addFirst(block);
                return false;
            }
        }
    }

    public void setWriteFinish(WriteFinish writeFinish) {
        this.writeFinish = writeFinish;
    }

    private void check() {
        if (buffer.isFull()) {
            buffer = freeBlock.poll();
            queue.add(buffer);
        }
    }

    @Override
    public void write(int b) {
        check();
        buffer.write(b);
        len++;
    }

    @Override
    public void write(byte[] b) {
        if (b == null) return;
        write(b, 0, b.length);
    }

    public void write(String s) {
        write(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (b == null) return;
        int write = 0;
        while (write < len) {
            check();
            write += buffer.write(b, write + off, len - write);
        }
        this.len += len;
    }

    public boolean available() {
        return !queue.getFirst().isEmpty();
    }

    public long size() {
        return len;
    }
}
