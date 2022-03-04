package cn.flandre.json.socket.stream;

import cn.flandre.json.exception.SystemBufferOverflowException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class BlockOutputStream extends OutputStream {
    private final FreeBlock freeBlock;
    private final LinkedList<Block> queue;
    private final SocksOutputStream os;
    private Block buffer;

    public BlockOutputStream(SocketChannel sc, FreeBlock freeBlock) {
        this.freeBlock = freeBlock;
        queue = new LinkedList<>();
        os = new SocksOutputStream(sc);
        buffer = freeBlock.poll();
        queue.add(buffer);
    }

    public boolean writeFully() throws IOException {
        int w;
        while (true) {
            Block block = queue.poll();  // 从写入队列取出块来写
            w = block.write(os);
            block.incPos(w);
            if (block.isEmpty()) {
                if (queue.size() != 0)
                    freeBlock.add(block);
                else {
                    block.reset();
                    queue.add(block);
                }
                try {
                    os.flush();
                    return true;
                } catch (SystemBufferOverflowException e) {
                    return false;
                }
            } else {
                queue.addFirst(block);
                return false;
            }
        }
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
    }

    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        int write = 0;
        while (write < len) {
            check();
            write += buffer.write(b, write + off, len - write);
        }
    }
}
