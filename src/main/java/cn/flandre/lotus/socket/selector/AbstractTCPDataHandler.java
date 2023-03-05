package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.socket.threadpool.Worker;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class AbstractTCPDataHandler {
    protected final SocketChannel sc;
    protected final Register register;
    protected final Worker worker;

    public AbstractTCPDataHandler(SocketChannel sc, Register register, Worker worker) {
        this.sc = sc;
        this.register = register;
        this.worker = worker;
    }

    abstract public void readData(SelectionKey key) throws IOException;

    abstract public void writeData(SelectionKey key) throws IOException;
}
