package cn.flandre.example.controller.user;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

public class UserIndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        try {
            context.getResponse().setFileBody("/templates/admin/user/index.ftl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
