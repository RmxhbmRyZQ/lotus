package cn.flandre.json.socket.selector;

import cn.flandre.json.socket.threadpool.Worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client extends AbstractSelect {
    private final SocketChannel sc;
    private final Register register;
    private final Worker worker;

    public Client(SocketChannel sc, Register register, Worker worker) {
        this.sc = sc;
        this.register = register;
        this.worker = worker;
    }

    @Override
    public void onRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer allocate = ByteBuffer.allocate(4096);
        int r = 0;
        while (sc.read(allocate) != 0) {
            r += allocate.limit();
            allocate.clear();
        }
        System.out.println(Thread.currentThread() + ":" + r + ":" + key.channel());
        key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        String html = "<body><form action=\"/\" method=\"post\" enctype=\"multipart/form-data\"><input type=\"file\" name=\"upload\"><input type=\"submit\" name=\"sub\" id=\"\"></form></body>";
        String response = "HTTP/1.1 200 OK\r\nContent-Length: " + html.length() + "\r\n\r\n" + html;
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put(response.getBytes(StandardCharsets.UTF_8));
        SocketChannel sc = (SocketChannel) key.channel();
        allocate.flip();
        sc.write(allocate);
        key.interestOps(SelectionKey.OP_READ);
    }
}
