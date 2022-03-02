package cn.flandre.json.socket.threadpool;

import java.util.LinkedList;

public class Handler extends Thread implements Postman {
    private final LinkedList<Message> messagesQueue = new LinkedList<>();
    private final MessageHandler handler;

    public Handler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public synchronized void sendMessage(Message message) {
        messagesQueue.add(message);
        if (messagesQueue.size() == 1) {
            notify();
        }
    }

    @Override
    public void run() {
        Message message;
        while (true) {
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
