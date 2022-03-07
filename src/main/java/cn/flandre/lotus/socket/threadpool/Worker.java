package cn.flandre.lotus.socket.threadpool;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.IOConstant;
import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.socket.selector.IOLoop;
import cn.flandre.lotus.socket.selector.RegisterItem;
import cn.flandre.lotus.socket.stream.FreeBlock;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class Worker extends Thread {
    private final IOLoop loop;
    private final int id;
    private final FreeBlock freeBlock = new FreeBlock();  // 每个线程一个的内存管理
    private Database database = null;

    public Worker(Postman postman, int id) throws IOException {
        this.id = id;
        loop = new IOLoop(() -> {
            postman.sendMessage(Message.obtain(IOConstant.CLOSE_SOCKET, id));
        });
        if (HttpApplication.setting.getUseDataBase()) {
            database = new Database();
        }
    }

    public boolean register(RegisterItem registerItem) {
        try {
            return loop.addRegister(registerItem);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
            return false;
        }
    }

    public IOLoop getLoop() {
        return loop;
    }

    @Override
    public void run() {
        try {
            loop.loop();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public int id() {
        return id;
    }

    public FreeBlock getFreeBlock() {
        return freeBlock;
    }

    public Database getDatabase() {
        return database;
    }
}
