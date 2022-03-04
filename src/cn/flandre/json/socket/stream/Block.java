package cn.flandre.json.socket.stream;

import cn.flandre.json.constant.IOConstant;
import cn.flandre.json.exception.SystemBufferOverflowException;

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

    public int read() {
        if (pos < limit) return bytes[pos++];
        return -1;
    }

    public int read(byte[] bytes, int offset, int len) {
        int min = Math.min(limit - pos, len);
        System.arraycopy(this.bytes, pos, bytes, offset, min);
        pos += min;
        return min;
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

    public void write(int b) {
        bytes[pos++] = (byte) b;
    }

    public int write(byte[] bytes, int off, int len){
        int min = Math.min(len, IOConstant.BLOCK_SIZE - limit);
        System.arraycopy(bytes, off, this.bytes, pos, min);
        limit += min;
        return min;
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

    public byte[] getBytes() {
        return bytes;
    }

    public int getPos() {
        return pos;
    }

    public int getLimit() {
        return limit;
    }
}
