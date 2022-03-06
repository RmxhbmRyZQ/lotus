package cn.flandre.lotus.exception;

public class HttpException extends RuntimeException{
    private final int status;
    private final boolean immediately;
    private String body;
    private boolean close = true;

    public HttpException(int status, boolean immediately) {
        this.status = status;
        this.immediately = immediately;
    }

    public HttpException(int status, boolean immediately, boolean close){
        this(status, immediately);
        this.close = close;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public boolean isImmediately() {
        return immediately;
    }
}
