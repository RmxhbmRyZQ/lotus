package cn.flandre.lotus.http.resolve;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.controller.Controller;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.LinkedList;

public class PathGroup {
    private static final LinkedList<Path> paths = new LinkedList<>();

    public static void addPath(Path path) {
        paths.add(path);
    }

    public static void addPath(String path, Controller controller){
        paths.add(new Path(path, controller));
    }

    public static boolean match(String uri, HttpContext context) {
        for (Path path : paths) {
            if (path.match(uri)) {
                return path.getPathPurification().handle(context, path.getMatcher());
            }
        }
        throw new HttpException(HttpState.NOT_FOUND, false, false);
    }
}
