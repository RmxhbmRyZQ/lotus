package cn.flandre.lotus.controller;

import cn.flandre.lotus.http.match.HttpContext;

import java.util.regex.Matcher;

public interface Controller {
    /**
     * 处理 get 请求
     * @param context 上下文
     * @param matcher 路径匹配的正则
     */
    public void get(HttpContext context, Matcher matcher);

    /**
     * 处理 post 请求
     * @param context 上下文
     * @param matcher 路径匹配的正则
     */
    public void post(HttpContext context, Matcher matcher);

    /**
     * 处理 head 请求
     * @param context 上下文
     * @param matcher 路径匹配的正则
     */
    public void head(HttpContext context, Matcher matcher);

    /**
     * 处理 delete 请求
     * @param context 上下文
     * @param matcher 路径匹配的正则
     */
    public void delete(HttpContext context, Matcher matcher);

    /**
     * 处理 put 请求
     * @param context 上下文
     * @param matcher 路径匹配的正则
     */
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
