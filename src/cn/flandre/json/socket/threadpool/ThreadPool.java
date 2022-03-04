package cn.flandre.json.socket.threadpool;

import cn.flandre.json.constant.IOConstant;
import cn.flandre.json.socket.selector.Client;
import cn.flandre.json.socket.selector.RegisterItem;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class ThreadPool implements Postman {
    private Handler handler;
    private final int[] taskNumber;
    private final LinkedList<Worker> freeWorker = new LinkedList<>();
    private final Worker[] workers;

    public ThreadPool() throws IOException {
        int core = Runtime.getRuntime().availableProcessors();  // 系统的线程数
        workers = new Worker[core];
        taskNumber = new int[core];
        initHandler();
        for (int i = 0; i < workers.length; i++) {  // 创建相应的线程
            workers[i] = new Worker(handler, i);
            freeWorker.add(workers[i]);
            workers[i].start();
        }
    }

    private void initHandler() {
        handler = new Handler(message -> {
            switch (message.what) {
                case IOConstant.DISTRIBUTE_SOCKET:  // 分配socket到合适的线程
                    distributeSocket((SocketChannel) message.obj);
                    break;
                case IOConstant.CLOSE_SOCKET:
                    closeSocket(message.arg1);
                    break;
            }
        });
        handler.start();
    }

    private void closeSocket(int id) {
        if (--taskNumber[id] == 0) {  // 当一个线程里面没有连接时，加入空线程队列
            freeWorker.add(workers[id]);
        }
    }

    private void distributeSocket(SocketChannel obj) {
        if (obj == null) return;
        Worker worker;
        if (freeWorker.size() > 0) {  // 有空闲线程
            worker = freeWorker.poll();
        } else {
            int id = 0, min = 0xffff;
            for (int i = 0; i < taskNumber.length; i++) {  // 使用任务最少得线程
                if (taskNumber[i] < min) {
                    id = i;
                    min = taskNumber[i];
                }
            }
            worker = workers[id];
        }
        taskNumber[worker.id()]++;
        Client client = new Client(obj, worker.getLoop(), worker.getFreeBlock());
        if (!worker.register(new RegisterItem(obj, SelectionKey.OP_READ, client))) {
            handler.sendMessage(Message.obtain(IOConstant.CLOSE_SOCKET, worker.id()));
        }
    }

    @Override
    public void sendMessage(Message message) {
        handler.sendMessage(message);
    }
}
