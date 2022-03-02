package cn.flandre.json.socket.threadpool;

import cn.flandre.json.constant.IOConstant;
import cn.flandre.json.socket.selector.IOLoop;
import cn.flandre.json.socket.selector.RegisterItem;
import cn.flandre.json.socket.stream.FreeBlock;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class Worker extends Thread {
    private final IOLoop loop;
    private final int id;
    private final FreeBlock freeBlock = new FreeBlock();

    public Worker(Postman postman, int id) throws IOException {
        this.id = id;
        loop = new IOLoop(() -> {
            postman.sendMessage(Message.obtain(IOConstant.CLOSE_SOCKET, id));
        });
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
}
