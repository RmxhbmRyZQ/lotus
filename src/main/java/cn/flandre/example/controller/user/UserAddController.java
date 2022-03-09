package cn.flandre.example.controller.user;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class UserAddController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", "");
        model.put("sex", 1);
        model.put("password", "");
        model.put("super", 0);
        model.put("id", "");
        model.put("type", "add");
        render("/templates/admin/user", "form.ftl", model, context);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        if ((boolean) context.getRequest().getExtra("operate"))
            context.getResponse().setBody("{\"code\":0,\"data\":[],\"message\":\"操作成功\"}");
        else
            context.getResponse().setBody("{\"code\":-1,\"data\":[],\"message\":\"操作失败\"}");
    }
}
