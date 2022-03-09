package cn.flandre.example.controller;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.session.Session;
import cn.flandre.lotus.http.web.Response;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

public class AdminWelcomeController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Session session = context.getRequest().getSession(context);
        if (session.getAttribute("userId") == null){
            redirect(context, "/admin/login/", false);
            return;
        }

        Response response = context.getResponse();
        try {
            response.setFileBody("/templates/admin/welcome.ftl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
