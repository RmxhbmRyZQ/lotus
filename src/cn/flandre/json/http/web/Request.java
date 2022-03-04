package cn.flandre.json.http.web;

import cn.flandre.json.exception.InvalidHttpHeaderException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    private final Map<String, String> headers = new HashMap<>();
    private final RequestCookie cookies;
    private final RequestMethod method;
    private final String path;
    private final String protocol;
    private byte[] content;

    public Request(String httpHeader) throws InvalidHttpHeaderException {
        String[] fields = httpHeader.split("\r\n");
        if (fields.length == 0)
            throw new InvalidHttpHeaderException();

        String[] head = fields[0].split(" ");
        if (head.length < 3)
            throw new InvalidHttpHeaderException();

        method = RequestMethod.parseString(head[0]);
        if (method == RequestMethod.IGNORANT_METHOD)
            throw new InvalidHttpHeaderException();

        path = head[1];
        protocol = head[2];
        if (!protocol.startsWith("HTTP/"))
            throw new InvalidHttpHeaderException();

        for (int i = 1; i < fields.length; i++) {
            String[] split = fields[i].split(":", 2);
            headers.put(split[0], split[1].trim());
        }

        cookies = new RequestCookie(headers.get("Cookie"));
        headers.remove("Cookie");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method.toString()).append(" ").append(path).append(" ").append(protocol).append("\r\n");


        Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
        Map.Entry<String, String> entry;

        if (iterator.hasNext()) {
            entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        while (iterator.hasNext()){
            entry = iterator.next();
            builder.append("\r\n").append(entry.getKey()).append("=").append(entry.getValue());
        }

        String cookie = cookies.toString();
        if (cookie != null) builder.append("\r\n").append(cookie);
        builder.append("\r\n\r\n");

        return builder.toString();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
