package cn.flandre.lotus.constant;

import cn.flandre.lotus.controller.FaviconController;
import cn.flandre.lotus.controller.IntroduceController;
import cn.flandre.lotus.controller.StaticController;
import cn.flandre.lotus.http.resolve.PathGroup;
import cn.flandre.lotus.json.JSONException;
import cn.flandre.lotus.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Setting {
    private final JSONObject setting = new JSONObject();

    public Setting() throws JSONException {
        setting.put("ip", "0.0.0.0");
        setting.put("port", 80);
        setting.put("maxHttpHead", 8 * 1024);  // 最大请求头
        setting.put("maxContent", 1024 * 1024 * 100);  // 最大请求体
        setting.put("keepAlive", true);
        setting.put("contentType", "text/html; charset=utf-8");
        setting.put("contentEncrypt", "identity");  // 压缩方式，仅支持 identity 和 gzip
        setting.put("minEncryptLength", 2 * 1024);  // 超过多少才进行压缩
        setting.put("maxEncryptLength", 10 * 1024 * 1024);  // 超过多少不再进行压缩
        setting.put("useDatabase", false);  // 是否使用数据库
        setting.put("useSession", false);  // 是否使用 session
        setting.put("sessionStore", "cache");  // session的储存位置，cache 内存，database 数据库
        setting.put("sessionExpireTime", 30 * 60 * 1000);  // session超时时间，30min
        setting.put("defaultResourcePath", "./src/main/resources");  // 资源存放路径
        // 如果useDatabase为true时，下面四个一定要定义
        setting.put("databaseDriver", "");
        setting.put("databaseUri", "");
        setting.put("databaseUsername", "");
        setting.put("databasePassword", "");
    }

    public Setting(String filename) throws JSONException, IOException {
        this();
        FileInputStream fileInputStream = new FileInputStream(filename);
        int available = fileInputStream.available();
        byte[] bytes = new byte[available];
        fileInputStream.read(bytes);
        String json = new String(bytes);
        JSONObject jsonObject = new JSONObject(json);
        for (Map.Entry<String, Object> entry : jsonObject.entry()) {
            setting.put(entry.getKey(), entry.getValue());
        }
    }

    public void init() {
        initPath();
        initMiddleware();
    }

    protected void initMiddleware() {

    }

    protected void initPath() {
        PathGroup.addPath("^/favicon.ico$", new FaviconController("/img/favicon.jpg"));
        PathGroup.addPath("^/static/(.+)", new StaticController("/static"));
        PathGroup.addPath(".*", new IntroduceController());
    }

    public int getMaxHttpHead() {
        return setting.getInt("maxHttpHead");
    }

    public boolean isKeepAlive() {
        return setting.getBoolean("keepAlive");
    }

    public int getPort() {
        return setting.getInt("port");
    }

    public String getIp() {
        return setting.getString("ip");
    }

    public int getMaxContent() {
        return setting.getInt("maxContent");
    }

    public String getContentType() {
        return setting.getString("contentType");
    }

    public String getContentEncrypt() {
        return setting.getString("contentEncrypt");
    }

    public int getMaxEncryptLength() {
        return setting.getInt("maxEncryptLength");
    }

    public int getMinEncryptLength() {
        return setting.getInt("minEncryptLength");
    }

    public String getSessionStore() {
        return setting.getString("sessionStore");
    }

    public String getDatabaseDriver() {
        return setting.getString("databaseDriver");
    }

    public boolean getUseDataBase() {
        return setting.getBoolean("useDatabase");
    }

    public boolean getUseSession() {
        return setting.getBoolean("useSession");
    }

    public int getSessionExpireTime() {
        return setting.getInt("sessionExpireTime");
    }

    public String getDefaultResourcePath() {
        return setting.getString("defaultResourcePath");
    }

    public Object get(String key) {
        return setting.get(key);
    }

    public int getInt(String key) {
        return setting.getInt(key);
    }

    public String getString(String key) {
        return setting.getString(key);
    }

    public double getDouble(String key) {
        return setting.getDouble(key);
    }

    public boolean getBoolean(String key) {
        return setting.getBoolean(key);
    }

    public long getLong(String key) {
        return setting.getLong(key);
    }
}
