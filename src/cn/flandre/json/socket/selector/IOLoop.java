package cn.flandre.json.socket.selector;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class IOLoop implements Register {
    private final Selector selector;
    private final LinkedList<RegisterItem> registerItems = new LinkedList<>();
    private OnClose close;
    private volatile boolean sleep = false;

    public IOLoop(OnClose close) throws IOException {
        this();
        this.close = close;
    }

    public IOLoop() throws IOException {
        selector = Selector.open();
    }

    private void add(RegisterItem register) {
        synchronized (this) {
            registerItems.add(register);
        }
        // 使用 while 是为了防止 wakeup 在 sleep 和 select 之间调用，从而形成死锁
        while (sleep)
            selector.wakeup();
    }

    @Override
    public boolean register(SelectableChannel channel, int even, OnSelect select)
            throws ClosedChannelException {
        if (!channel.isOpen()) return false;
//        if (sleep) add(new RegisterItem((SocketChannel) channel, even, select));
//        else
            channel.register(selector, even, select);
        return true;
    }

    @Override
    public boolean addRegister(RegisterItem registerItem) throws ClosedChannelException {
        if (!registerItem.getSc().isOpen()) return false;
//        if (sleep)
            add(registerItem);
//        else registerItem.register(this);
        return true;
    }

    public void loop() throws IOException {
        int select;
        while (true) {
            sleep = true;
            if (registerItems.size() > 0) {
                synchronized (this) {
                    for (RegisterItem registerItem : registerItems) {
                        registerItem.register(this);
                    }
                    registerItems.clear();
                }
            }
            select = selector.select();
            sleep = false;
            if (select == 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                callback(key);
                it.remove();
            }
        }
    }

    public void callback(SelectionKey key) {
        OnSelect select = (OnSelect) key.attachment();
        if (select == null)
            return;
        try {
            if (key.isAcceptable())  // accept 事件
                select.onAccept(key);
            if (key.isConnectable())  // connect 事件
                select.onConnect(key);
            if (key.isReadable())  // read 事件
                select.onRead(key);
            if (key.isWritable())  // write 事件
                select.onWrite(key);
        } catch (CancelledKeyException | IOException e) {
//            e.printStackTrace();
            select.onError(key, e);
            cancel(key.channel());
        }
    }

    private void cancel(SelectableChannel channel) {
        if (channel == null || !channel.isOpen()) return;
        if (close != null) close.onClose();
        try {
            channel.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
