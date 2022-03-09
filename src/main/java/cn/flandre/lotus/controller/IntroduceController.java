package cn.flandre.lotus.controller;

import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Response;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

public class IntroduceController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Response response = context.getResponse();
        try {
            response.setFileBody("/html/index.html");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
