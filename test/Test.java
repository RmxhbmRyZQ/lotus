import cn.flandre.json.socket.threadpool.Handler;
import cn.flandre.json.socket.threadpool.Message;
import cn.flandre.json.socket.threadpool.MessageHandler;

public class Test {
    public static final Object lock = new Object();

    public static void main(String[] args) {
        Handler handler = new Handler(System.out::println);
        handler.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("send message");
        handler.sendMessage(Message.obtain(1, "wadasdw"));
    }
}
