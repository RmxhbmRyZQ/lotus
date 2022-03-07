package cn.flandre.lotus.http.web;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 响应头的Cookie
 */
public class ResponseCookie {
    private final LinkedList<SetCookieItem> cookies = new LinkedList<>();

    public void addCookie(SetCookieItem cookieItem) {
        cookies.add(cookieItem);
    }

    public void removeCookie(SetCookieItem cookieItem) {
        cookies.remove(cookieItem);
    }

    public int size() {
        return cookies.size();
    }

    @Override
    public String toString() {
        if (cookies.size() == 0) return null;
        StringBuilder builder = new StringBuilder();
        Iterator<SetCookieItem> iterator = cookies.iterator();
        SetCookieItem cookieItem;

        if (iterator.hasNext()) {
            cookieItem = iterator.next();
            builder.append("Set-Cookie: ").append(cookieItem.toString());
        }

        while (iterator.hasNext()) {
            cookieItem = iterator.next();
            builder.append("\r\n").append("Set-Cookie: ").append(cookieItem.toString());
        }

        return builder.toString();
    }
}
