package cn.flandre.json.http.resolve;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public Cookie(String cookie) {
        if (cookie == null) return;
        String[] split = cookie.split(";");
        for (String item : split) {
            String[] items = item.trim().split("=");
            cookies.put(items[0], items[1]);
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

    public String toString(String head) {
        if (cookies.size() == 0) return null;
        StringBuilder builder = new StringBuilder();
        builder.append(head);
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
}
