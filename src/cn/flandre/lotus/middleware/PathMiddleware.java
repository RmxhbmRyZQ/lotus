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

    public void handle(HttpContext context, Matcher matcher) {
        if (in != null)
            for (Pipeline pipeline : in) {
                if (pipeline.distribute(context, matcher)) {
                    return;
                }
            }

        controller.distribute(context, matcher);

        if (out != null)
            for (Pipeline pipeline : out) {
                if (pipeline.distribute(context, matcher)) {
                    return;
                }
            }
    }
}
