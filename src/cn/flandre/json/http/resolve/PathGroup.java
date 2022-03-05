package cn.flandre.json.http.resolve;

import cn.flandre.json.http.match.HttpContext;

import java.util.LinkedList;

public class PathGroup {
    private static final LinkedList<Path> paths = new LinkedList<>();

    public static void addPath(Path path) {
        paths.add(path);
    }

    public static void match(String uri, HttpContext context) {
        for (Path path : paths) {
            if (path.match(uri)) {
                path.getPathPurification().handle(context, path.getMatcher());
                return;
            }
        }
    }
}
