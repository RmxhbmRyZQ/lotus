package cn.flandre.lotus.http.resolve;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.controller.Controller;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.middleware.PathMiddlewareBean;
import cn.flandre.lotus.middleware.Pipeline;

import java.util.LinkedList;

public class PathGroup {
    private static final LinkedList<Path> paths = new LinkedList<>();

    public static void addPath(Path path) {
        paths.add(path);
    }

    public static void addPath(String path, LinkedList<Pipeline> in, LinkedList<Pipeline> out, Controller controller) {
        paths.add(new Path(path, new PathMiddlewareBean(in, out, controller)));
    }

    public static void addPath(String path, Pipeline in, Pipeline out, Controller controller) {
        LinkedList<Pipeline> ins = null;
        LinkedList<Pipeline> outs = null;
        if (in != null) {
            ins = new LinkedList<>();
            ins.add(in);
        }
        if (out != null) {
            outs = new LinkedList<>();
            outs.add(in);
        }
        paths.add(new Path(path, new PathMiddlewareBean(ins, outs, controller)));
    }

    public static void addPath(String path, Controller controller) {
        paths.add(new Path(path, controller));
    }

    public static boolean match(String uri, HttpContext context) {
        for (Path path : paths) {
            if (path.match(uri)) {
                return path.getPathPurification().handle(context, path.getMatcher());
            }
        }
        throw new HttpException(HttpState.NOT_FOUND, false);
    }
}
