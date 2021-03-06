package cn.flandre.lotus.socket.selector;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;

public interface Register {
    public boolean register(SelectableChannel channel, int even, OnSelect select) throws ClosedChannelException;

    public boolean addRegister(RegisterItem r) throws ClosedChannelException;

    public void cancel(SelectableChannel channel);
}
