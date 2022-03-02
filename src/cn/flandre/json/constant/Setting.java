package cn.flandre.json.constant;

public class Setting {
    private String ip = "0.0.0.0";
    private int port = 80;

    private static final Setting setting = initSetting();

    private static Setting initSetting(){
        return new Setting();
    }

    public static Setting getSetting() {
        return setting;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
