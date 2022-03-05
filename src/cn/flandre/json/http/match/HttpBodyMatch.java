package cn.flandre.json.http.match;

import cn.flandre.json.http.resolve.PathGroup;
import cn.flandre.json.middleware.GlobalMiddleware;
import cn.flandre.json.middleware.Pipeline;
import cn.flandre.json.socket.stream.Block;

import java.nio.channels.SelectionKey;

public class HttpBodyMatch implements Match {
    private int require;
    private final HttpContext context;
    private int len = 0;

    public HttpBodyMatch(HttpContext context) {
        this.context = context;
    }

    public void setRequire(int require) {
        this.require = require;
    }

    @Override
    public void match(int read, Block block, int offset) {
        byte[] bytes = null;
        if (require != 0) {
            len += read;
            if (len >= require) {
                bytes = new byte[require];
                context.getBis().read(bytes);
            } else return;
        }

        int r = len - require;

        WriteFinish writeFinish = context.getWriteFinish();
        writeFinish.setBlock(block);
        writeFinish.setRead(r);
        writeFinish.setOffset(offset + read - r);

        produceData(bytes);
    }

    public void produceData(byte[] content) {
        context.getRequest().setContent(content);
        PathGroup.match(context.getRequest().getPath(), context);

        for (Pipeline pipeline : GlobalMiddleware.out){
            if (pipeline.distribute(context, null))
                return;
        }

        // 生成发送信息
        context.getResponse().write(context.getBos());
        context.getKey().interestOps(SelectionKey.OP_WRITE);
    }
}
