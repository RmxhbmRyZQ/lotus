package cn.flandre.json.socket.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class BlockInputStream extends InputStream {
    private final FreeBlock freeBlock;
    private Block buffer;
    private Block readBuffer = null;
    private final LinkedList<Block> queue;
    private final SocksInputStream is;

    public BlockInputStream(SocketChannel sc, FreeBlock freeBlock) {
        this.freeBlock = freeBlock;
        queue = new LinkedList<>();
        is = new SocksInputStream(sc);
        buffer = freeBlock.poll();
        queue.add(buffer);
    }

//    public void setMatch(byte[] delimiter, callback);

    public int readFully() throws IOException {
        int len = 0, r;
        while (true) {
            r = buffer.read(is);
            if (r == -1) {
                if (len == 0)
                    len = -1;
                break;  // EOF
            }
            match();
            len += r;
            buffer.incLimit(r);
            if (buffer.isFull()) {  // 如果写满了放入相应的写入队列
//                queue.add(buffer);
                buffer = freeBlock.poll();
                
            }
        }
//        return len == 0 ? -1 : len;
        return len;
    }

    private void match() {
        InputStreamReader reader = new InputStreamReader(this);
    }

    @Override
    public int read() throws IOException {
        if (readBuffer == null) readBuffer = queue.poll();
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }
}
