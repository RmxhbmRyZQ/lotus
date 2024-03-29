package cn.flandre.lotus.http.match;

import cn.flandre.lotus.http.resolve.PathGroup;
import cn.flandre.lotus.middleware.GlobalMiddlewareBean;
import cn.flandre.lotus.middleware.Pipeline;
import cn.flandre.lotus.socket.stream.Block;

import java.nio.channels.SelectionKey;

public class HttpBodyMatch implements Match {
    private int require;  // 请求体长度
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
        len += read;
        if (require != 0) {
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
        // 设置请求体
        context.getRequest().setContent(content);

        boolean skip = false;
        for (Pipeline pipeline : GlobalMiddlewareBean.in) {  // 全局入中间件
            if ((skip = pipeline.distribute(context, null))) {
                break;
            }
        }

        if (!skip && !PathGroup.match(context.getRequest().getPath(), context)) {  // 路径分发
            for (Pipeline pipeline : GlobalMiddlewareBean.out) {  // 全局出中间件
                if (pipeline.distribute(context, null))
                    break;
            }
        }

        context.getRequest().releaseSession(context.getResponse());
        // 设置响应体大小
        context.getResponse().addHead("Content-Length", String.valueOf(context.getResponse().getBodyLength()));
        // 生成发送信息
        context.getResponse().write(context.getBos());
        context.getKey().interestOps(SelectionKey.OP_WRITE);
    }
}
