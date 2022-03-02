package cn.flandre.json.socket.stream;

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
    }

    public boolean writeFully() throws IOException {
        int w;
        while (true) {
            Block block = queue.poll();  // 从写入队列取出块来写
            if (block == null) {  // 写如队列为空，写 BUFFER
                if (buffer.isEmpty()) break;
                w = buffer.write(os);
                buffer.incPos(w);
                if (buffer.isEmpty()) buffer.reset();
                else  return false;
                break;
            }
            w = block.write(os);
            block.incPos(w);
            if (block.isEmpty()) {
                freeBlock.add(block);
            } else {
                queue.addFirst(block);
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }
}
