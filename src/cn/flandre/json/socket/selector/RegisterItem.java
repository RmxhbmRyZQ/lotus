package cn.flandre.json.socket.selector;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public class RegisterItem {
    private SocketChannel sc;
    private int even;
    private OnSelect select;

    public RegisterItem(SocketChannel sc, int even, OnSelect select) {
        this.sc = sc;
        this.even = even;
        this.select = select;
    }

    public SocketChannel getSc() {
        return sc;
    }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }

    public int getEven() {
        return even;
    }

    public void setEven(int even) {
        this.even = even;
    }

    public OnSelect getSelect() {
        return select;
    }

    public void setSelect(OnSelect select) {
        this.select = select;
    }

    public void register(Register register) throws ClosedChannelException {
        register.register(sc, even, select);
    }
}
