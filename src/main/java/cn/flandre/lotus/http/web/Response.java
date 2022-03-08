package cn.flandre.lotus.http.web;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.ContentType;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.socket.stream.BlockOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private int status = HttpState.OK;
    private final Map<String, String> headers = new HashMap<>();
    private final ResponseCookie cookies = new ResponseCookie();
    private final ResponseBody body;

    public Response(BlockOutputStream body) {
        this.body = new ResponseBody(this, body);
    }

    /**
     * 添加响应头
     */
    public void addHead(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 删除响应头
     */
    public void removeHead(String key) {
        headers.remove(key);
    }

    /**
     * 获取响应头
     */
    public String getHead(String key) {
        String head = headers.get(key);
        return head != null ? head : "";
    }

    /**
     * 设置cookie
     */
    public void setCookie(SetCookieItem item) {
        cookies.addCookie(item);
    }

    /**
     * 删除cookie
     */
    public void removeCookie(SetCookieItem item) {
        cookies.removeCookie(item);
    }

    /**
     * 设置状态码以及对应的响应体
     */
    public void setStatusWithBody(int status) {
        setStatus(status);
        String builder = "<h1>" + status + "</h1>" +
                "<h2>" + HttpState.getStatusDescription(status) + "</h2>";
        setBody(builder);
    }

    /**
     * 设置状态码
     */
    public void setStatus(int status) {
        if (HttpState.isAvailableStatus(status))
            this.status = status;
    }

    public void write(BlockOutputStream bos) {
        bos.write("HTTP/1.1 ");
        bos.write(String.valueOf(status));
        bos.write(' ');
        bos.write(HttpState.getStatusDescription(status));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            bos.write("\r\n" + entry.getKey() + ": " + entry.getValue());
        }
        String cookie = cookies.toString();
        if (cookie != null) {
            bos.write("\r\n");
            bos.write(cookies.toString());
        }
        bos.write("\r\n\r\n");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(status).append(" ").append(HttpState.getStatusDescription(status));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append("\r\n").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        if (cookies.size() > 0) {
            builder.append("\r\n").append(cookies);
        }
        builder.append("\r\n\r\n");
        return builder.toString();
    }

    public boolean shouldTransferTo() {
        return body.shouldTransferTo();
    }

    public boolean transfer(SocketChannel channel) throws IOException {
        return body.transfer(channel);
    }

    /**
     * 设置json类型的响应体
     */
    public void setJsonBody(String jsonBody){
        setBody(jsonBody);
        headers.put("Content-Type", ContentType.getContentType(".json"));
    }

    /**
     * 设置文件类型的响应体
     */
    public void setTextBody(String textBody){
        setBody(textBody);
        headers.put("Content-Type", ContentType.getContentType(".txt"));
    }

    /**
     * 设置html类型的响应体
     */
    public void setHtmlBody(String htmlBody){
        setBody(htmlBody);
        headers.put("Content-Type", ContentType.getContentType(".html"));
    }

    public void setBody(byte[] body) {
        this.body.setBody(body);
    }

    /**
     * 设置响应体
     */
    public void setBody(String body) {
        this.body.setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 设置文件为响应体
     */
    public void setFileBody(File file) throws FileNotFoundException {
        this.body.setFileBody(file);
    }

    public void setFileBody(String file) throws FileNotFoundException {
        this.body.setFileBody(new File(file));
    }

    public void setEncrypt(String header) {
        if (header == null) return;
        if (header.contains(HttpApplication.setting.getContentEncrypt())) {
            body.setEncrypt(true);
        }
    }

    public void finish(OutputStream os) throws IOException {
        body.finish(os);
    }

    public OutputStream getOS(String path, String filename) {
        return body.getOS(path, filename);
    }

    public boolean shouldWriteBody() {
        return body.shouldWriteBody();
    }

    public long getBodyLength() {
        return body.length();
    }
}
