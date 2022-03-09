package cn.flandre.example.controller;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Session session = context.getRequest().getSession(context);
        if (session.getAttribute("userId") == null){
            redirect(context, "/admin/login/", false);
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("username", session.getAttribute("username"));
        render("/templates/admin", "index.ftl", model, context);
    }
}
