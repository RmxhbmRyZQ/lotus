package cn.flandre.example.controller.login;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class LoginController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();

        if ((int) request.getExtra("userId") != -1) {
            redirect(context, "/admin/index/", false);
            return;
        }

        render("/templates/admin/", "login.ftl", null, context);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        int id = (int) request.getExtra("userId");
        if (id == -1) {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "invalid username or password");
            render("/templates/admin/", "login.ftl", model, context);
            return;
        }

        redirect(context, "/admin/index/", false);
    }
}
