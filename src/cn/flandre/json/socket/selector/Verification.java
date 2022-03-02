package cn.flandre.json.socket.selector;

import java.nio.channels.SocketChannel;

public interface Verification {
    public boolean verify(SocketChannel sc);
}
