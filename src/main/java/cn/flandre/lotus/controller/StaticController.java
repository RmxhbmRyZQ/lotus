package cn.flandre.lotus.controller;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.ContentType;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;

/**
 * 静态文件控制器
 */
public class StaticController extends BaseController {
    private final String base;

    /**
     * @param base 静态文件存放的路径，相对于project文件路径来说
     */
    public StaticController(String base) {
        this.base = base;
    }

    public StaticController() {
        this("");
    }

    @Override
    public void get(HttpContext context, Matcher matcher) {
        Response response = context.getResponse();
        String path = matcher.group(1);
        if (path.contains("./")) {
            throw new HttpException(HttpState.BAD_REQUEST, false);
        }

        path = HttpApplication.setting.getDefaultResourcePath() + base + "/" + path;

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
            throw new HttpException(HttpState.NOT_FOUND, false);
        }
    }
}
