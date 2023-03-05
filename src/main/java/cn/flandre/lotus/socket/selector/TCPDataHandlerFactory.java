package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.http.match.HttpConnection;
import cn.flandre.lotus.socket.threadpool.Worker;

import java.nio.channels.SocketChannel;

public class TCPDataHandlerFactory {
    public static AbstractTCPDataHandler createHandler(SocketChannel sc, Register register, Worker worker) {
        return new HttpConnection(sc, register, worker);
    }
}
