package cn.flandre.lotus.socket.threadpool;

/**
 * 消息队列的发送器
 */
public interface Postman {
    public void sendMessage(Message m);
}
