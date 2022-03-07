package cn.flandre.lotus.exception;

public class HttpException extends RuntimeException {
    private boolean immediately = false;  // 是否立即关闭socket
    private int status;  // 响应码
    private String body;  // 响应体
    private boolean close = true;  // 发送错误后是否关闭socket

    public HttpException() {
        immediately = true;
    }

    public HttpException(int status) {
        this.status = status;
    }

    public HttpException(int status, boolean close) {
        this(status);
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
