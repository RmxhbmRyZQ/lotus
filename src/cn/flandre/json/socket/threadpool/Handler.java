package cn.flandre.json.socket.threadpool;

import java.util.LinkedList;

public class Handler extends Thread implements Postman {
    private final LinkedList<Message> messagesQueue = new LinkedList<>();  // 消息队列
    private final MessageHandler handler;

    public Handler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public synchronized void sendMessage(Message message) {  // 发送消息
        messagesQueue.add(message);
        if (messagesQueue.size() == 1) {
            notify();
        }
    }

    @Override
    public void run() {
        Message message;
        while (true) {  // 处理消息
            synchronized (this) {
                if (messagesQueue.size() == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                message = messagesQueue.pop();
            }
            handler.handler(message);
        }
    }
}
