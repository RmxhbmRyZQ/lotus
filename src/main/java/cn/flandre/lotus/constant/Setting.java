package cn.flandre.lotus.constant;

import cn.flandre.lotus.json.JSONException;
import cn.flandre.lotus.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public abstract class Setting {
    private final JSONObject setting = new JSONObject();

    public Setting() throws JSONException {
        setting.put("ip", "0.0.0.0");
        setting.put("port", 80);
        setting.put("maxHttpHead", 8 * 1024);
        setting.put("maxContent", 1024 * 1024 * 100);
        setting.put("keepAlive", true);
        setting.put("contentType", "text/html; charset=utf-8");
        setting.put("contentEncrypt", "identity");
        setting.put("minEncryptLength", 2 * 1024);
        setting.put("maxEncryptLength", 10 * 1024 * 1024);
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

    public abstract void initPath();

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
}
