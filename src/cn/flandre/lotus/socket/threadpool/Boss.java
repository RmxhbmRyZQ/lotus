package cn.flandre.lotus.socket.threadpool;

import cn.flandre.lotus.socket.selector.IOLoop;
import cn.flandre.lotus.socket.selector.Server;

import java.io.IOException;

/**
 * 主线程，负责accept连接
 */
public class Boss {
    private final IOLoop loop;
    private final ThreadPool pool = new ThreadPool();

    public Boss() throws IOException {
        loop = new IOLoop();
    }

    public void loop() throws IOException {
        Server server = new Server(loop, pool);
        server.register();
        loop.loop();
    }
}
