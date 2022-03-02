package cn.flandre.json.socket.stream;

import cn.flandre.json.socket.exception.SystemBufferOverflowException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Block {
    private final byte[] bytes;
    private int pos;
    private int limit;

    public Block(byte[] bytes, int pos, int limit) {
        this.bytes = bytes;
        this.pos = pos;
        this.limit = limit;
    }

    public void reset() {
        pos = 0;
        limit = 0;
    }

    public void incPos(int pos) {
        this.pos += pos;
    }

    public int read(InputStream is) throws IOException {
        return is.read(bytes, limit, bytes.length - limit);
    }

    public int read(byte[] bytes, int offset, int len){
        return 0;
    }

    public int write(OutputStream os) throws IOException {
        int len = limit - pos;
        try {
            os.write(bytes, pos, len);
        } catch (SystemBufferOverflowException e) {
            len = e.getLen();
        }
        return len;
    }

    public void incLimit(int r) {
        limit += r;
    }

    public boolean isFull() {
        return limit == bytes.length;
    }

    public boolean isEmpty() {
        return pos == limit;
    }
}
