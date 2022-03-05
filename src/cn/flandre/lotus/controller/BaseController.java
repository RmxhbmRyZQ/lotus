package cn.flandre.lotus.controller;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Response;

import java.util.regex.Matcher;

public class BaseController implements Controller {
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

    protected void redirect(HttpContext context, String path, boolean permanent) {
        Response response = context.getResponse();
        response.setStatus(permanent ? HttpState.MOVED_PERMANENTLY : HttpState.FOUND);
        response.addHead("Location", path);
    }
}
