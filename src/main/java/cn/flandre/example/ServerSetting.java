package cn.flandre.example;

import cn.flandre.example.controller.*;
import cn.flandre.example.controller.login.LoginController;
import cn.flandre.example.controller.login.LogoutController;
import cn.flandre.example.controller.user.*;
import cn.flandre.example.middleware.*;
import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.controller.FaviconController;
import cn.flandre.lotus.controller.StaticController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.resolve.PathGroup;

import java.io.IOException;
import java.util.regex.Matcher;

public class ServerSetting extends Setting {
    public ServerSetting(String s) throws IOException {
        super(s);
    }

    public static void main(String[] args) throws IOException {
        HttpApplication.run(new ServerSetting("./server.json"));
    }

    @Override
    protected void initPath() {
        PathGroup.addPath("^/static/(.+)", new StaticController("/static"));
        PathGroup.addPath("^/favicon.ico$", new FaviconController("/img/favicon.jpg"));
        PathGroup.addPath("^/admin/login/?(.*)$", new LoginMiddleware(), null, new LoginController());
        PathGroup.addPath("^/admin/logout/?(.*)$", new LogoutController());
        PathGroup.addPath("^/admin/index/?(.*)$", new IndexController());
        PathGroup.addPath("^/admin/welcome/?(.*)$", new AdminWelcomeController());
        PathGroup.addPath("^/admin/user/?(index)?/?$", new UserIndexController());
        PathGroup.addPath("^/admin/user/list/?$", new UserListMiddleware(), null, new UserListController());
        PathGroup.addPath("^/admin/user/edit/?(\\d*)$", new UserEditMiddleware(), null, new UserEditController());
        PathGroup.addPath("^/admin/user/add/?$", new UserAddMiddleware(), null, new UserAddController());
        PathGroup.addPath("^/admin/user/delete/(\\d+)$", new UserDeleteMiddleware(), null, new UserDeleteController());
        PathGroup.addPath("^(.*?)$", new BaseController(){
            @Override
            public void get(HttpContext context, Matcher matcher) {
                redirect(context, "/admin/index/", true);
            }
        });
    }
}
