package cn.flandre.lotus.http.web;

import java.util.Date;

public class SetCookieItem {
    private final String key;
    private final String value;
    private Date expires;  // 过期时间:DAY, DD MMM YYYY HH:MM:SS GMT
    private String domain;  // 域名
    private String path;  // 路径
    private boolean secure;
    private boolean httpOnly;

    public SetCookieItem(String key, String value, Date expires, String domain, String path, boolean secure, boolean httpOnly) {
        this.key = key;
        this.value = value;
        this.expires = expires;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public SetCookieItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SetCookieItem(String key, String value, Date expires) {
        this.key = key;
        this.value = value;
        this.expires = expires;
    }

    public SetCookieItem(String key, String value, String path) {
        this.key = key;
        this.value = value;
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(key).append("=").append(value);
        if (expires != null)
            builder.append("; expires=").append(expires.toGMTString());
        if (domain != null)
            builder.append("; domain=").append(domain);
        if (path != null)
            builder.append("; path=").append(path);
        if (secure)
            builder.append("; secure");
        if (httpOnly)
            builder.append("; httponly");

        return builder.toString();
    }
}
