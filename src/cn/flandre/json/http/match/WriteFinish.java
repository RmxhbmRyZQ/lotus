package cn.flandre.json.http.match;

import cn.flandre.json.socket.stream.Block;

import java.nio.channels.SelectionKey;

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
        HttpHeaderMatch match = context.getHttpHeaderMatch();
        String connection;
        // 不支持长连接就关闭Socket
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
