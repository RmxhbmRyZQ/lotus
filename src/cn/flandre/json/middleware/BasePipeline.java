package cn.flandre.json.middleware;

import cn.flandre.json.constant.HttpState;
import cn.flandre.json.http.match.HttpContext;

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
    public boolean put(HttpState constant, Matcher matcher) {
        return false;
    }
}
