package cn.flandre.example.controller.user;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.Map;
import java.util.regex.Matcher;

public class UserEditController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Object user = context.getRequest().getExtra("user");
        if (user != null) {
            render("/templates/admin/user", "form.ftl", (Map<String, Object>) user, context);
        }
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        if ((boolean) context.getRequest().getExtra("operate"))
            context.getResponse().setBody("{\"code\":0,\"data\":[],\"message\":\"操作成功\"}");
        else
            context.getResponse().setBody("{\"code\":-1,\"data\":[],\"message\":\"操作失败\"}");
    }
}
