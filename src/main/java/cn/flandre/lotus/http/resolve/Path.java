package cn.flandre.lotus.http.resolve;

import cn.flandre.lotus.controller.Controller;
import cn.flandre.lotus.middleware.PathMiddlewareBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    private final Pattern pattern;
    private final PathMiddlewareBean pathMiddlewareBean;
    private Matcher matcher;

    public Path(Pattern pattern, PathMiddlewareBean pathMiddlewareBean) {
        this.pattern = pattern;
        this.pathMiddlewareBean = pathMiddlewareBean;
    }

    public Path(Pattern pattern, Controller controller) {
        this.pattern = pattern;
        this.pathMiddlewareBean = new PathMiddlewareBean(controller);
    }

    public Path(String pattern, Controller controller) {
        this.pattern = Pattern.compile(pattern);
        this.pathMiddlewareBean = new PathMiddlewareBean(controller);
    }

    public Path(String pattern, PathMiddlewareBean pathMiddlewareBean) {
        this.pattern = Pattern.compile(pattern);
        this.pathMiddlewareBean = pathMiddlewareBean;
    }

    public boolean match(String uri) {
        matcher = pattern.matcher(uri);
        return matcher.find();
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public PathMiddlewareBean getPathPurification() {
        return pathMiddlewareBean;
    }
}
