package cn.flandre.lotus.middleware;

import cn.flandre.lotus.controller.Controller;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.LinkedList;
import java.util.regex.Matcher;

public class PathMiddleware {
    private final LinkedList<Pipeline> in;
    private final LinkedList<Pipeline> out;
    private final Controller controller;

    public PathMiddleware(LinkedList<Pipeline> in, LinkedList<Pipeline> out, Controller controller) {
        this.in = in;
        this.out = out;
        this.controller = controller;
    }

    public PathMiddleware(Controller controller){
        this(null, null, controller);
    }

    public boolean handle(HttpContext context, Matcher matcher) {
        if (in != null)
            for (Pipeline pipeline : in) {  // 路径入中间件
                if (pipeline.distribute(context, matcher)) {
                    return true;
                }
            }

        controller.distribute(context, matcher);  // 控制器

        if (out != null)
            for (Pipeline pipeline : out) {  // 路径出中间件
                if (pipeline.distribute(context, matcher)) {
                    return true;
                }
            }
        return false;
    }
}
