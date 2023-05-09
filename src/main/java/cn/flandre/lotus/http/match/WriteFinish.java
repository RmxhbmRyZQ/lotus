package cn.flandre.lotus.http.match;

import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.socket.selector.Register;
import cn.flandre.lotus.socket.stream.Block;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteFinish {
    private final HttpContext context;
    private int read;
    private int offset;
    private Block block;
    private boolean close = false;  // 是否关闭socket

    public WriteFinish(HttpContext context) {
        this.context = context;
    }

    public void writeFinish() {
        Response response = context.getResponse();
        SelectionKey key = context.getKey();
        Register register = context.getRegister();

        // 发送响应体
        if (response.shouldWriteBody()) {
            try {
                if (!context.getResponseBody().writeFully()) {
                    return;
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                register.cancel(key.channel());
            }
        } else if (response.shouldTransferTo()) {  // 如果需要发送文件，没在公网测试过，不知道有没有错误
            try {
                if (!response.transfer((SocketChannel) context.getKey().channel())) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                register.cancel(key.channel());  // 出错，暂时就断开连接先吧
            }
        }

        if (close) {
            register.cancel(key.channel());
            return;
        }

        // 如果服务器不支持长连接就关闭socket
        if (!response.getHead("Connection").equalsIgnoreCase("keep-alive")) {
            register.cancel(key.channel());
            return;
        }

        // 如果客户端不支持长连接就关闭Socket
        HttpHeaderMatch match = context.getHttpHeaderMatch();
        String connection;
        if ((connection = context.getRequest().getHeader("Connection")) == null ||
                !connection.equalsIgnoreCase("keep-alive")) {
            register.cancel(key.channel());
            return;
        }
        context.getBis().setMatch(match);

        if (read == 0) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }
        // 如果缓存有下一个请求头信息，则进行处理
        match.match(read, block, offset);
        if (!context.getBos().available())
            key.interestOps(SelectionKey.OP_READ);
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
