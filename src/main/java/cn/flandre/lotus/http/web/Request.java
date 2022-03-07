package cn.flandre.lotus.http.web;

import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    private final Map<String, String> headers = new HashMap<>();
    private final RequestCookie cookies;
    private final RequestMethod method;
    private final String path;
    private final Map<String, String> params = new HashMap<>();
    private final String protocol;
    private final Map<String, Object> extra = new HashMap<>();
    private static final byte[] sep = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

    public Request(String httpHeader) {
        String[] fields = httpHeader.split("\r\n");
        if (fields.length == 0)
            throw new HttpException(HttpState.BAD_REQUEST);

        String[] head = fields[0].split(" ");
        if (head.length < 3)
            throw new HttpException(HttpState.BAD_REQUEST);

        method = RequestMethod.parseString(head[0]);
        if (method == RequestMethod.IGNORANT_METHOD)
            throw new HttpException(HttpState.BAD_REQUEST);

        String uri = head[1];
        int find = uri.indexOf('?');
        if (find != -1) {
            path = uri.substring(0, find);
            uri = uri.substring(find + 1);
            setKeyValues(uri, params);
        } else path = uri;

        protocol = head[2];
        if (!protocol.startsWith("HTTP/"))
            throw new HttpException(HttpState.HTTP_VERSION_NOT_SUPPORTED);

        // 解析 HTTP 请求头键值对
        for (int i = 1; i < fields.length; i++) {
            String[] split = fields[i].split(":", 2);
            headers.put(split[0], split[1].trim());
        }

        cookies = new RequestCookie(headers.get("Cookie"));
        headers.remove("Cookie");
    }

    private void setKeyValues(String param, Map<String, String> map) {
        int find, end, start = 0;
        String item;
        while ((end = param.indexOf('&', start)) != -1) {
            item = param.substring(start, end);
            find = item.indexOf('=');
            if (find != -1) {
                map.put(item.substring(0, find), item.substring(find + 1));
            }
            start = end + 1;
        }
        item = param.substring(start);
        find = item.indexOf('=');
        if (find != -1) {
            map.put(item.substring(0, find), item.substring(find + 1));
        }
    }

    private String join(Map<String, String> map, String inner, String line) {
        if (map == null || map.size() == 0) return "";
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        Map.Entry<String, String> entry;

        if (iterator.hasNext()) {
            entry = iterator.next();
            builder.append(entry.getKey()).append(inner).append(entry.getValue());
        }
        while (iterator.hasNext()) {
            entry = iterator.next();
            builder.append(line).append(entry.getKey()).append(inner).append(entry.getValue());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method.toString()).append(" ").append(path).append("?")
                .append(join(params, "=", "&")).append(" ").append(protocol).append("\r\n")
                .append(join(headers, "=", "\r\n"));

        String cookie = cookies.toString();
        if (cookie != null) builder.append("\r\n").append(cookie);
        builder.append("\r\n\r\n");

        return builder.toString();
    }

    /**
     * 获取Uri上的键值对
     */
    public String getParam(String key) {
        return params.get(key);
    }

    /**
     * 获取请求头
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * 获取Cookie
     */
    public String getCookie(String key) {
        return cookies.get(key);
    }

    /**
     * 获取请求的方法
     */
    public RequestMethod getMethod() {
        return method;
    }

    /**
     * 获取uri
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取协议
     */
    public String getProtocol() {
        return protocol;
    }

    private byte[] content;
    private Map<String, String> normalBody;
    private Map<String, MultipartData> fileBody;

    private int indexOf(byte[] src, int offset, byte[] match) {
        byte first = match[0];
        int max = src.length - match.length;
        for (; offset <= max; offset++) {
            if (src[offset] != first) {
                while (++offset < max && src[offset] != first) ;
            }

            if (offset <= max) {
                int j = offset + 1;
                int end = j + match.length - 1;
                for (int k = 1; j < end && src[j] == match[k]; j++, k++) ;

                if (j == end) {
                    return j;
                }
            }
        }
        return -1;
    }

    public void setContent(byte[] content) {
        if (content == null) return;
        String contentType = headers.get("Content-Type");
        if (contentType.startsWith("application/x-www-form-urlencoded")) {  // 键值对
            normalBody = new HashMap<>();
            setKeyValues(new String(content), normalBody);
        } else if (contentType.startsWith("multipart/form-data")) {  // 文件上传
            byte[] boundary = contentType.substring(contentType.indexOf("boundary") + 9).getBytes(StandardCharsets.UTF_8);
            int offset = 0, start, end = 0;
            fileBody = new HashMap<>();
            MultipartData data = null;

            while ((start = indexOf(content, offset, boundary)) != -1) {
                if (data != null) {
                    int len = start - boundary.length - 4 - end;  // -- + boundary
                    if (data.getFilename() != null) {
                        data.setFileItem(new MultipartData.FileItem(end, len, content));
                    } else {
                        data.setValue(new String(content, end, len));
                    }
                }

                offset = start + 2;  // \r\n
                end = indexOf(content, offset, sep);
                if (end == -1) break;
                String head = new String(content, offset, end - sep.length - offset);
                data = new MultipartData(head);
                fileBody.put(data.getKey(), data);
            }
        } else this.content = content;  // 其他请求如JSON
    }

    /**
     * 获取键值对的请求体
     */
    public String getNormalBody(String key) {
        if (normalBody == null) return null;
        return normalBody.get(key);
    }

    /**
     * 获取文件上传的请求体
     */
    public MultipartData getFileBody(String key) {
        if (fileBody == null) return null;
        return fileBody.get(key);
    }

    public byte[] getContent() {
        return content;
    }

    /**
     * 设置额外信息
     */
    public void putExtra(String key, Object value) {
        extra.put(key, value);
    }

    /**
     * 获取额外信息
     */
    public Object getExtra(String key) {
        return extra.get(key);
    }

    /**
     * 获取额外的键值对
     */
    public Map<String, Object> getExtras() {
        return extra;
    }
}
