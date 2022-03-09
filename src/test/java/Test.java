import cn.flandre.lotus.socket.stream.BlockOutputStream;
import cn.flandre.lotus.socket.stream.FreeBlock;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("username", "admin");
            Template template = getFreeMarkerCFG("./src/main/resources/templates/admin").getTemplate("index.ftl");
            Writer out = new OutputStreamWriter(new ByteArrayOutputStream());
//            Writer out = new OutputStreamWriter(new BlockOutputStream(null, new FreeBlock()));
            template.process(model, out);
            int breakPoint = 0;
        } catch (IOException | TemplateException ioException) {
            ioException.printStackTrace();
        }
    }

    private static volatile Configuration configuration = null;

    protected static Configuration getFreeMarkerCFG(String sTemplateFilePath) {
        if (null == configuration) {
            synchronized (Test.class) {
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
