package cn.flandre.lotus.middleware;

import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.regex.Matcher;

public interface Pipeline {
    public boolean get(HttpContext context, Matcher matcher);

    public boolean post(HttpContext context, Matcher matcher);

    public boolean head(HttpContext context, Matcher matcher);

    public boolean delete(HttpContext context, Matcher matcher);

    public boolean put(HttpContext context, Matcher matcher);

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
                throw new HttpException(HttpState.METHOD_NOT_ALLOWED, false);
        }
    }
}
