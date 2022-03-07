package cn.flandre.lotus.middleware;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.regex.Matcher;

public class BasePipeline implements Pipeline {
    @Override
    public boolean get(HttpContext context, Matcher matcher) {
        return false;
    }

    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        return false;
    }

    @Override
    public boolean head(HttpContext context, Matcher matcher) {
        return false;
    }

    @Override
    public boolean delete(HttpContext context, Matcher matcher) {
        return false;
    }

    @Override
    public boolean put(HttpContext context, Matcher matcher) {
        return false;
    }
}
