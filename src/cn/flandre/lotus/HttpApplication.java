package cn.flandre.lotus;

import cn.flandre.lotus.socket.threadpool.Boss;

import java.io.IOException;

public class HttpApplication {
    public static void main(String[] args) {
        try {
            new Boss().loop();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
