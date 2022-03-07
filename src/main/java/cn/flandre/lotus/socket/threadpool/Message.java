package cn.flandre.lotus.socket.threadpool;

import java.util.LinkedList;

/**
 * 消息队列的消息
 */
public class Message {
    private static final LinkedList<Message> freeQueue = new LinkedList<>();

    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    private synchronized static Message obtain() {
        Message message;
        if (freeQueue.size() > 0)
            message = freeQueue.pop();
        else
            message = new Message();
        return message;
    }

    public static Message obtain(int what, Object o) {
        Message message = obtain();
        message.what = what;
        message.obj = o;
        return message;
    }

    public static Message obtain(int what, int arg1) {
        Message message = obtain();
        message.what = what;
        message.arg1 = arg1;
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "what=" + what +
                ", arg1=" + arg1 +
                ", arg2=" + arg2 +
                ", obj=" + obj +
                '}';
    }
}
