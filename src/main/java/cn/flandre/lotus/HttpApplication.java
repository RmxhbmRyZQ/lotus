package cn.flandre.lotus;

import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.socket.threadpool.Boss;

import java.io.IOException;

public class HttpApplication {
    public static Setting setting;

    public static void run(Setting setting) {
        HttpApplication.setting = setting;
        setting.init();
        try {
            new Boss().loop();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("http://127.0.0.1/");
        run(new Setting());
    }
}
