package cn.flandre.json.exception;

import java.io.IOException;

public class SystemBufferOverflowException extends IOException {
    private int len;

    public SystemBufferOverflowException(int len) {
        this.len = len;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
