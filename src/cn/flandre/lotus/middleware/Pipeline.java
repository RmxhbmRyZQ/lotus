package cn.flandre.lotus.middleware;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.regex.Matcher;

public interface Pipeline {
    public boolean get(HttpContext context, Matcher matcher);

    public boolean post(HttpContext context, Matcher matcher);

    public boolean head(HttpContext context, Matcher matcher);

    public boolean delete(HttpContext context, Matcher matcher);

    public boolean put(HttpState constant, Matcher matcher);

    public default boolean distribute(HttpContext context, Matcher matcher) {
        switch (context.getRequest().getMethod()) {
            case GET:
                return get(context, matcher);
            case POST:
                return post(context, matcher);
            case HEAD:
                return head(context, matcher);
            case PUT:
                return post(context, matcher);
            case DELETE:
                return delete(context, matcher);
            default:
                return disconnect(context, HttpState.BAD_REQUEST);
        }
    }

    public default boolean disconnect(HttpContext context) {
        context.getRegister().cancel(context.getKey().channel());
        return true;
    }

    public default boolean disconnect(HttpContext context, int status) {
        context.getResponse().setStatusWithBody(status);
        context.getWriteFinish().setClose(true);
        return true;
    }
}
