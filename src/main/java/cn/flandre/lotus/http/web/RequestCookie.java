package cn.flandre.lotus.http.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RequestCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public RequestCookie(String cookie) {
        if (cookie == null) return;
        String[] split = cookie.split(";");
        for (String item : split) {
            String[] items = item.trim().split("=");
            if (items.length == 2)
                cookies.put(items[0], items[1]);
            else cookies.put(items[0], "");
        }
    }

    public void put(String key, String value) {
        cookies.put(key, value);
    }

    public void remove(String key) {
        cookies.remove(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public Set<Map.Entry<String, String>> entry() {
        return cookies.entrySet();
    }

    @Override
    public String toString() {
        if (cookies.size() == 0) return null;
        StringBuilder builder = new StringBuilder();
        builder.append("Cookie: ");
        Iterator<Map.Entry<String, String>> iterator = entry().iterator();
        Map.Entry<String, String> entry;

        if (iterator.hasNext()) {
            entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        while (iterator.hasNext()) {
            entry = iterator.next();
            builder.append("; ").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

    public int size() {
        return cookies.size();
    }
}
