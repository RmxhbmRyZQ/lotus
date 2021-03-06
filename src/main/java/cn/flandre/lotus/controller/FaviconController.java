package cn.flandre.lotus.controller;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.http.match.HttpContext;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

/**
 * /favicon.ico 的处理控制器
 */
public class FaviconController extends BaseController{
    private final String path;

    /**
     * @param path favicon.ico 的存放位置
     */
    public FaviconController(String path) {
        this.path = path;
    }

    @Override
    public void get(HttpContext context, Matcher matcher) {
        try {
            context.getResponse().setFileBody(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new HttpException(HttpState.NOT_FOUND, false);
        }
    }
}
