package cn.flandre.json.middleware;

import cn.flandre.json.controller.Controller;
import cn.flandre.json.http.match.HttpContext;

import java.util.LinkedList;

public class PathMiddleware {
    private final LinkedList<Pipeline> in;
    private final LinkedList<Pipeline> out;
    private final Controller controller;

    public PathMiddleware(LinkedList<Pipeline> in, LinkedList<Pipeline> out, Controller controller) {
        this.in = in;
        this.out = out;
        this.controller = controller;
    }

    public void solve(HttpContext context) {
        if (in != null)
            for (Pipeline pipeline : in) {
                if (pipeline.handle(context)) {
                    return;
                }
            }

        controller.controller(context);

        if (out != null)
            for (Pipeline pipeline : out) {
                if (pipeline.handle(context)) {
                    return;
                }
            }
    }
}
