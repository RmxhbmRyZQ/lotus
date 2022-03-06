package cn.flandre.lotus.middleware;

import java.util.LinkedList;

public class GlobalMiddleware {
    public final static LinkedList<Pipeline> in = new LinkedList<>();  // 在解析完request head时调用
    public final static LinkedList<Pipeline> out = new LinkedList<>();

    public static void addIn(Pipeline pipeline){
        in.add(pipeline);
    }

    public static void addOut(Pipeline pipeline){
        out.add(pipeline);
    }

    public static void removeIn(Pipeline pipeline){
        in.remove(pipeline);
    }

    public static void removeOut(Pipeline pipeline){
        out.remove(pipeline);
    }
}
