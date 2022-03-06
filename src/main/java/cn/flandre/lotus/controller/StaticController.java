package cn.flandre.lotus.controller;

import cn.flandre.lotus.constant.ContentType;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;

public class StaticController extends BaseController {
    private final String base;

    public StaticController(String base) {
        this.base = base;
    }

    public StaticController(){
        this("");
    }

    @Override
    public void get(HttpContext context, Matcher matcher) {
        Response response = context.getResponse();
        String path = matcher.group(1);
        if (path.contains("./")) {
            throw new HttpException(HttpState.BAD_REQUEST, false, false);
        }
        path = "." + File.separator + base + File.separator + path;

        String suffix;
        int find = path.lastIndexOf('.');
        if (find == -1)
            suffix = ".*";
        else
            suffix = path.substring(find);

        try {
            response.setFileBody(path);
            response.addHead("Content-Type", ContentType.getContentType(suffix));
        } catch (FileNotFoundException e) {
            throw new HttpException(HttpState.NOT_FOUND, false, false);
        }
    }
}
