package cn.flandre.json.http.web;

import cn.flandre.json.constant.HttpConstant;
import cn.flandre.json.socket.stream.BlockOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();
    private final ResponseCookie cookies = new ResponseCookie();
    private byte[] body;
    private File fileBody;

    public void addHead(String key, String value) {
        headers.put(key, value);
    }

    public void removeHead(String key) {
        headers.remove(key);
    }

    public String getHead(String key) {
        return headers.get(key);
    }

    public void setCookie(SetCookieItem item) {
        cookies.addCookie(item);
    }

    public void removeCookie(SetCookieItem item) {
        cookies.removeCookie(item);
    }

    public void setBody(String body) {
        headers.put("Content-Length", String.valueOf(body.length()));
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public void setBody(byte[] body) {
        headers.put("Content-Length", String.valueOf(body.length));
        this.body = body;
    }

    public void setStatus(int status) {
        if (HttpConstant.isAvailableStatus(status))
            this.status = status;
    }

    public void write(BlockOutputStream bos) {
        bos.write("HTTP/1.1 ");
        bos.write(String.valueOf(status));
        bos.write(' ');
        bos.write(HttpConstant.getStatusDescription(status));
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
        else if (fileBody != null) {  // 文件上传，这里写的不好以后重构，先搞明白FileChannel
            try {
                FileInputStream fileInputStream = new FileInputStream(fileBody);
                byte[] bytes = new byte[1024];
                int read;
                while ((read = fileInputStream.read(bytes)) != -1) {
                    bos.write(bytes, 0, read);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(status).append(" ").append(HttpConstant.getStatusDescription(status));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append("\r\n").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        if (cookies.size() > 0) {
            builder.append("\r\n").append(cookies);
        }
        builder.append("\r\n\r\n").append(new String(body));
        return builder.toString();
    }

    public void setBody(File file) {
        headers.put("Content-Length", String.valueOf(file.length()));
        fileBody = file;
    }
}
