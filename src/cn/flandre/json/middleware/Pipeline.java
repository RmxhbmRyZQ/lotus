package cn.flandre.json.middleware;

import cn.flandre.json.http.match.HttpContext;

public interface Pipeline {
    /**
     * @return true:
     * 1.数据出现问题，断开连接，需要调用代码
     * context.getRegister().cancel(context.getSocketChannel());
     *
     * 2.数据出现问题，返回完错误信息后断开连接，需要调用代码
     * key.interestOps(SelectionKey.OP_WRITE);
     * context.getWriteFinish().setClose(true);
     */
    public boolean handle(HttpContext context);
}
