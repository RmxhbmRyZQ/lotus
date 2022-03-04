package cn.flandre.json.socket.stream;

import cn.flandre.json.http.resolve.Match;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class BlockInputStream extends InputStream {
    private final FreeBlock freeBlock;
    private Block buffer;
    private final LinkedList<Block> queue;
    private final InputStream is;
    private Match match = null;

    public BlockInputStream(SocketChannel sc, FreeBlock freeBlock) {
        this.freeBlock = freeBlock;
        queue = new LinkedList<>();
        is = new SocksInputStream(sc);
        buffer = freeBlock.poll();
        queue.add(buffer);
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int readFully(SelectionKey key) throws IOException {
        if (match == null) throw new IOException("must call setMatch first");
        int len = 0, r, off;
        while (true) {
            off = buffer.getPos();
            r = buffer.read(is);
            if (r == -1) {
                if (len == 0)
                    len = -1;
                break;  // EOF
            }
            len += r;
            buffer.incLimit(r);
            if (buffer.isFull()) {  // 如果写满取空的出来
                buffer = freeBlock.poll();
                queue.add(buffer);
            }
            match.match(r, buffer, off, key);
        }
        return len;
    }

    @Override
    public int read() {
        Block block;
        while ((block = queue.getFirst()).isEmpty()) {
            if (queue.size() == 1) {
                block.reset();
                return -1;
            }
            freeBlock.add(queue.removeFirst());
        }
        return block.read();
    }

    @Override
    public int read(byte[] b) {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int read = 0, r;
        Block block;
        out:
        while (read < len) {
            while ((block = queue.getFirst()).isEmpty()) {
                if (queue.size() == 1) {
                    block.reset();
                    break out;
                }
                freeBlock.add(queue.removeFirst());
            }
            r = block.read(b, read + off, len - read);
            read += r;
        }
        return read == 0 ? -1 : read;
    }
}
