package example.controller;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Map<String, String> map = new HashMap<>();
        map.put("name", "john");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        geneHtmlFile("./template", "index.html", map, baos);
        context.getResponse().setBody(baos.toByteArray());
    }

    private Configuration freemarke_cfg = null;

    protected Configuration getFreeMarkerCFG(String sTemplateFilePath) {

        if (null == freemarke_cfg) {
            freemarke_cfg = new Configuration();
            freemarke_cfg.setEncoding(Locale.CHINA, "UTF-8");
            //基于类路径的模版加载器
            freemarke_cfg.setClassForTemplateLoading(this.getClass(), "/websiteroot/freemarker");
            try {
                freemarke_cfg.setDirectoryForTemplateLoading(new File(sTemplateFilePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return freemarke_cfg;
    }

    /**
     * 生成静态文件
     *
     * @param templatePath:模板路径
     * @param templateFileName:模板名称
     * @param propMap:存放数据模型的Map
     */
    public boolean geneHtmlFile(String templatePath, String templateFileName, Map propMap, OutputStream os) {
        try {
            Template t = getFreeMarkerCFG(templatePath).getTemplate(templateFileName);
            t.setEncoding("UTF-8");

            //设置生成的文件编码为UTF-8
            //服务器不支持UTF-8格式HTML时候使用ANSI格式HTML文件，即系统默认编码
            Writer out = new OutputStreamWriter(os, "UTF-8");
            t.process(propMap, out);
        } catch (TemplateException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean creatDirs(String path) {
        File aFile = new File(path);
        if (!aFile.exists()) {
            return aFile.mkdirs();
        } else {
            return true;
        }
    }


}
