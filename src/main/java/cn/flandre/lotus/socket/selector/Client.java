package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.socket.threadpool.Worker;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 连接的封装处理，由于多路复用的关系，不需要进行线程同步的处理
 */
public class Client extends AbstractSelect {
    private final AbstractTCPDataHandler tcpDataHandler;

    public Client(SocketChannel sc, Register register, Worker worker) {
        tcpDataHandler = TCPDataHandlerFactory.createHandler(sc, register, worker);
    }

    @Override
    public void onRead(SelectionKey key) throws IOException {
        tcpDataHandler.readData(key);
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        tcpDataHandler.writeData(key);
    }
}
