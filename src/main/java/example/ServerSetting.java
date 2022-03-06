package example;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.controller.StaticController;
import cn.flandre.lotus.http.resolve.PathGroup;
import example.controller.IndexController;

import java.io.IOException;

public class ServerSetting extends Setting {
    public ServerSetting(String s) throws IOException {
        super(s);
    }

    public static void main(String[] args) throws IOException {
        HttpApplication.run(new ServerSetting("./server.json"));
    }

    @Override
    public void initPath() {
        PathGroup.addPath("/index/?$", new IndexController());
        PathGroup.addPath("/static/(.+)", new StaticController("static"));
    }
}
