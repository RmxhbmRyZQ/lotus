package cn.flandre.lotus.http.web;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.exception.ResponseBodyAlreadySetException;
import cn.flandre.lotus.socket.stream.BlockOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private int status = HttpState.OK;
    private final Map<String, String> headers = new HashMap<>();
    private final ResponseCookie cookies = new ResponseCookie();
    private byte[] body;

    public void addHead(String key, String value) {
        headers.put(key, value);
    }

    public void removeHead(String key) {
        headers.remove(key);
    }

    public String getHead(String key) {
        String head = headers.get(key);
        return head != null ? head : "";
    }

    public void setCookie(SetCookieItem item) {
        cookies.addCookie(item);
    }

    public void removeCookie(SetCookieItem item) {
        cookies.removeCookie(item);
    }

    public void setStatusWithBody(int status) {
        setStatus(status);
        String builder = "<h1>" + status + "</h1>" +
                "<h2>" + HttpState.getStatusDescription(status) + "</h2>";
        setBody(builder);
    }

    public void setBody(String body) {
        if (fileBody != null)
            throw new ResponseBodyAlreadySetException("Cannot set body, when the filebody have been set");
        headers.put("Content-Length", String.valueOf(body.length()));
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public void setBody(byte[] body) {
        if (fileBody != null)
            throw new ResponseBodyAlreadySetException("Cannot set body, when the filebody have been set");
        headers.put("Content-Length", String.valueOf(body.length));
        this.body = body;
    }

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
        if (body != null)
            bos.write(body);
    }

    private File fileBody;
    private long fileLength;
    private long transfer;
    private FileChannel fileChannel;

    public void setFileBody(File file) throws FileNotFoundException {
        if (body != null)
            throw new ResponseBodyAlreadySetException("Cannot set filebody, when the body have been set");
        if (file == null || !file.exists())
            throw new FileNotFoundException();
        fileBody = file;
        fileLength = fileBody.length();
        headers.put("Content-Length", String.valueOf(fileLength));
    }

    public void setFileBody(String file) throws FileNotFoundException {
        setFileBody(new File(file));
    }

    public boolean shouldTransferTo() {
        if (fileBody != null) {
            try {
                fileChannel = new FileInputStream(fileBody).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean transfer(WritableByteChannel channel) throws IOException {
        // 想使用 zip 压缩，但又不能进行 transfer 了
        transfer += fileChannel.transferTo(transfer, fileLength - transfer, channel);
        boolean finish = transfer == fileLength;
        if (finish) fileChannel.close();
        return finish;
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
        if (body != null)
            builder.append(new String(body));
        return builder.toString();
    }
}
