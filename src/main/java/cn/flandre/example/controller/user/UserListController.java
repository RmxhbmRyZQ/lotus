package cn.flandre.example.controller.user;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.regex.Matcher;

public class UserListController extends BaseController {
    @Override
    public void post(HttpContext context, Matcher matcher) {
        Object userlist = context.getRequest().getExtra("userlist");
        if (userlist != null)
            context.getResponse().setBody(userlist.toString());
    }
}
