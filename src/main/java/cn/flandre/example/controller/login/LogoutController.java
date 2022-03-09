package cn.flandre.example.controller.login;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.session.Session;
import cn.flandre.lotus.http.web.Request;

import java.util.regex.Matcher;

public class LogoutController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        Session session = request.getSession(context);
        session.removeAttribute("userId");
        session.removeAttribute("username");
        session.removeAttribute("password");
        redirect(context, "/admin/login/", false);
    }
}
