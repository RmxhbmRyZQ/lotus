package cn.flandre.lotus.controller;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Response;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 基础控制器，写控制器时首要继承类
 */
public class BaseController implements Controller {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        throw new HttpException(HttpState.NOT_FOUND, false);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        throw new HttpException(HttpState.NOT_FOUND, false);
    }

    @Override
    public void head(HttpContext context, Matcher matcher) {
        throw new HttpException(HttpState.NOT_FOUND, false);
    }

    @Override
    public void delete(HttpContext context, Matcher matcher) {
        throw new HttpException(HttpState.NOT_FOUND, false);
    }

    @Override
    public void put(HttpContext context, Matcher matcher) {
        throw new HttpException(HttpState.NOT_FOUND, false);
    }

    /**
     * 页面跳转
     *
     * @param context   上下文
     * @param path      跳转的路径
     * @param permanent true 301, false 302
     */
    protected void redirect(HttpContext context, String path, boolean permanent) {
        Response response = context.getResponse();
        response.setStatus(permanent ? HttpState.MOVED_PERMANENTLY : HttpState.FOUND);
        response.addHead("Location", path);
    }

    /**
     * 模板渲染，使用freemarker
     *
     * @param path     模板路径，相对于Setting.defaultResourcePath
     * @param filename 模板名称
     * @param model    数据模型
     * @param context  上下文
     */
    protected void render(String path, String filename, Map<String, Object> model, HttpContext context) {
        try {
            if (path.equals("")){
                path = HttpApplication.setting.getDefaultResourcePath();
            }else {
                path = HttpApplication.setting.getDefaultResourcePath() + path;
            }
            Template template = getFreeMarkerCFG(path).getTemplate(filename);
            OutputStream os = context.getResponse().getOS(path, filename);
            Writer out = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            template.process(model, out);
            context.getResponse().finish(os);
        } catch (TemplateException | IOException e) {
            throw new HttpException(HttpState.NOT_FOUND, false);
        }
    }

    private volatile Configuration configuration = null;

    protected Configuration getFreeMarkerCFG(String sTemplateFilePath) {
        if (null == configuration) {
            synchronized (this) {
                if (null == configuration) {
                    configuration = new Configuration(Configuration.getVersion());
                    configuration.setEncoding(Locale.CHINA, "UTF-8");

                    // configuration.setClassForTemplateLoading(this.getClass(), "/");  // 根据类的路径加载
                    try {
                        configuration.setDirectoryForTemplateLoading(new File(sTemplateFilePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return configuration;
    }
}
