package cn.flandre.lotus.http.match;

import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.http.resolve.PathGroup;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.middleware.GlobalMiddleware;
import cn.flandre.lotus.middleware.Pipeline;
import cn.flandre.lotus.socket.stream.Block;

import java.nio.channels.SelectionKey;
import java.util.Date;

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

        produceHead();
        produceData(bytes);
    }

    private void produceHead() {
        Setting setting = Setting.getSetting();
        Response response = context.getResponse();
        response.addHead("Server", "lotus");
        response.addHead("Connection", setting.isKeepAlive() ? "keep-alive" : "close");
        response.addHead("Date", new Date().toString());
        response.addHead("Content-Type", setting.getContentType());
        response.addHead("X-Powered-By", "Java/1.8");
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
