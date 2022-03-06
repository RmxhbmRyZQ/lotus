package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.IOConstant;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.socket.threadpool.Message;
import cn.flandre.lotus.socket.threadpool.Postman;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server extends AbstractSelect {
    private final Register register;
    private final Postman postman;
    private final ServerSocketChannel ssc;
    private final Security security = new Security();

    public Server(Register register, Postman postman) throws IOException {
        this.register = register;
        this.postman = postman;
        ssc = ServerSocketChannel.open();
        Setting setting = HttpApplication.setting;
        configureServerSocket(ssc, new InetSocketAddress(setting.getIp(), setting.getPort()));
    }

    @Override
    public void onAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = serverSocketChannel.accept();
        configureSocket(sc);
        if (security.verify(sc)) {  // 通过验证后，把连接扔给线程池分配
            Message obtain = Message.obtain(IOConstant.DISTRIBUTE_SOCKET, sc);
            postman.sendMessage(obtain);
        } else {
            sc.close();
        }
    }

    public void register() throws ClosedChannelException {
        register.register(ssc, SelectionKey.OP_ACCEPT, this);
    }
}
