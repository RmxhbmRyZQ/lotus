package cn.flandre.json.http.resolve;

import cn.flandre.json.middleware.PathMiddleware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    private final Pattern pattern;
    private final PathMiddleware pathMiddleware;
    private Matcher matcher;

    public Path(Pattern pattern, PathMiddleware pathMiddleware) {
        this.pattern = pattern;
        this.pathMiddleware = pathMiddleware;
    }

    public Path(String pattern, PathMiddleware pathMiddleware) {
        this.pattern = Pattern.compile(pattern);
        this.pathMiddleware = pathMiddleware;
    }

    public boolean match(String uri) {
        matcher = pattern.matcher(uri);
        return matcher.find();
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public PathMiddleware getPathPurification() {
        return pathMiddleware;
    }
}
