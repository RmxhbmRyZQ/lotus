package cn.flandre.json.controller;

import cn.flandre.json.constant.HttpState;
import cn.flandre.json.http.match.HttpContext;

import java.util.regex.Matcher;

public class BaseController implements Controller{
    @Override
    public void get(HttpContext context, Matcher matcher) {
        context.getResponse().setStatusWithBody(HttpState.METHOD_NOT_ALLOWED);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        context.getResponse().setStatusWithBody(HttpState.METHOD_NOT_ALLOWED);
    }

    @Override
    public void head(HttpContext context, Matcher matcher) {
        context.getResponse().setStatusWithBody(HttpState.METHOD_NOT_ALLOWED);
    }

    @Override
    public void delete(HttpContext context, Matcher matcher) {
        context.getResponse().setStatusWithBody(HttpState.METHOD_NOT_ALLOWED);
    }

    @Override
    public void put(HttpContext context, Matcher matcher) {
        context.getResponse().setStatusWithBody(HttpState.METHOD_NOT_ALLOWED);
    }
}
