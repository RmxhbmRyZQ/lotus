package cn.flandre.lotus.constant;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.controller.StaticController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.resolve.Path;
import cn.flandre.lotus.http.resolve.PathGroup;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.http.web.SetCookieItem;
import cn.flandre.lotus.middleware.PathMiddleware;
import com.sun.jndi.toolkit.url.UrlUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;

public class Setting {
    private String ip = "0.0.0.0";
    private int port = 80;
    private int maxContent = 1024 * 1024 * 100;
    private boolean keepAlive = true;
    private String contentType = "text/html; charset=utf-8";

    static {
        PathGroup.addPath(new Path("/static/(.+)", new PathMiddleware(null, null, new StaticController(""))));
        PathGroup.addPath(new Path("/index/(.+)", new PathMiddleware(null, null, new BaseController() {
            @Override
            public void get(HttpContext context, Matcher matcher) {
                Response response = context.getResponse();
                response.setCookie(new SetCookieItem("name", "john"));
                String decode = "";
                try {
                    decode = UrlUtil.decode(matcher.group(1));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String name = context.getRequest().getCookie("name");
                if (name != null)
                    response.setBody("<h1>hello " + name + "</h1><h2>welcome to " + decode + "</h2>");
                else {
                    response.setBody("<h2>welcome to " + decode + "</h2>");
                }
            }
        })));
        PathGroup.addPath(new Path("/favicon.ico", new PathMiddleware(null, null, new BaseController() {
            @Override
            public void get(HttpContext context, Matcher matcher) {
                Response response = context.getResponse();
                File file = new File("D:\\BaiduNetdiskDownload\\芙兰朵露\\50492988_p0_master1200.jpg");
                try {
                    response.setFileBody(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    response.setStatusWithBody(HttpState.NOT_FOUND);
                }
            }
        })));
        PathGroup.addPath(new Path("/*.", new PathMiddleware(null, null, new BaseController() {
            @Override
            public void get(HttpContext context, Matcher matcher) {
                redirect(context, "/index/awdasd", false);
            }
        })));
    }

    private static final Setting setting = initSetting();

    public boolean isKeepAlive() {
        return keepAlive;
    }

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

    public int getMaxContent() {
        return maxContent;
    }

    public String getContentType() {
        return contentType;
    }
}
