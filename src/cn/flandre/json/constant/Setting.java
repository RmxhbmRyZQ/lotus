package cn.flandre.json.constant;

import cn.flandre.json.http.resolve.Path;
import cn.flandre.json.http.resolve.PathGroup;
import cn.flandre.json.http.web.Response;
import cn.flandre.json.http.web.SetCookieItem;
import cn.flandre.json.middleware.PathMiddleware;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

public class Setting {
    private String ip = "0.0.0.0";
    private int port = 80;

    static {
        PathGroup.addPath(new Path("/index", new PathMiddleware(null, null, (context)->{
            Response response = context.getResponse();
            response.setCookie(new SetCookieItem("name", "john", new Date(0)));
            response.setBody("<body><form action=\"/\" method=\"post\" enctype=\"multipart/form-data\"><input type=\"file\" name=\"upload\"><input type=\"submit\" name=\"sub\" id=\"\"></form></body>");
        })));
        PathGroup.addPath(new Path("/favicon.ico", new PathMiddleware(null, null, (context)->{
            Response response = context.getResponse();
            File file = new File("C:\\Users\\RmxhbmRyZQ\\Desktop\\png\\0.png");
            response.setBody(file);
        })));
        PathGroup.addPath(new Path("/.*", new PathMiddleware(null, null, (context -> {
            Response response = context.getResponse();
            response.addHead("Connection", "keep-alive");
            response.setStatus(404);
            response.setBody("Not Found");
        }))));
    }

    private static final Setting setting = initSetting();

    private static Setting initSetting() {
        return new Setting();
    }

    public static Setting getSetting() {
        return setting;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
