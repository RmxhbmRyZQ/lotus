package cn.flandre.json.controller;

import cn.flandre.json.http.match.HttpContext;

import java.util.regex.Matcher;

public interface Controller {
    public void get(HttpContext context, Matcher matcher);

    public void post(HttpContext context, Matcher matcher);

    public void head(HttpContext context, Matcher matcher);

    public void delete(HttpContext context, Matcher matcher);

    public void put(HttpContext context, Matcher matcher);

    public default void distribute(HttpContext context, Matcher matcher) {
        switch (context.getRequest().getMethod()) {
            case GET:
                get(context, matcher);
                break;
            case POST:
                post(context, matcher);
                break;
            case HEAD:
                head(context, matcher);
                break;
            case PUT:
                put(context, matcher);
                break;
            case DELETE:
                delete(context, matcher);
                break;
        }
    }
}
