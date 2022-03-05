package cn.flandre.lotus.http.match;

import cn.flandre.lotus.socket.stream.Block;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteFinish {
    private final HttpContext context;
    private int read;
    private int offset;
    private Block block;
    private boolean close = false;

    public WriteFinish(HttpContext context) {
        this.context = context;
    }

    public void writeFinish() {
        SelectionKey key = context.getKey();
        if (close) {
            context.getRegister().cancel(key.channel());
            return;
        }

        // 如果需要发送文件，没在公网测试过，不知道有没有错误
        if (context.getResponse().shouldTransferTo()) {
            try {
                if (!context.getResponse().transfer((SocketChannel) context.getKey().channel())) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                context.getRegister().cancel(key.channel());  // 出错，暂时就断开连接先吧
            }
        }

        if (!context.getResponse().getHead("Connection").equalsIgnoreCase("keep-alive")){
            context.getRegister().cancel(key.channel());
            return;
        }

        // 不支持长连接就关闭Socket
        HttpHeaderMatch match = context.getHttpHeaderMatch();
        String connection;
        if ((connection = context.getRequest().getHeader("Connection")) == null ||
                !connection.equalsIgnoreCase("keep-live")) {
            context.getRegister().cancel(key.channel());
            return;
        }

        if (read == 0) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }
        // 读了下一个请求的信息
        context.getBis().setMatch(match);
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
