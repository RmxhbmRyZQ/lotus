package cn.flandre.lotus.http.match;

import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.socket.selector.AbstractTCPDataHandler;
import cn.flandre.lotus.socket.selector.Register;
import cn.flandre.lotus.socket.stream.BlockInputStream;
import cn.flandre.lotus.socket.stream.BlockOutputStream;
import cn.flandre.lotus.socket.stream.SocksOutputStream;
import cn.flandre.lotus.socket.threadpool.Worker;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Http连接，负责处理TCP的数据，并转换成HTTP包
 */
public class HttpConnection extends AbstractTCPDataHandler {
    private final BlockInputStream bis;
    private final BlockOutputStream bos;
    private final HttpContext context;

    public HttpConnection(SocketChannel sc, Register register, Worker worker) {
        super(sc, register, worker);
        SocksOutputStream sos = new SocksOutputStream(sc);
        bis = new BlockInputStream(sc, worker.getFreeBlock());
        bos = new BlockOutputStream(sos, worker.getFreeBlock());

        // 初始化连接的上下文
        context = new HttpContext(bis, bos, register, new BlockOutputStream(sos, worker.getFreeBlock()));
        context.setDatabase(worker.getDatabase());
        HttpHeaderMatch httpHeaderMatch = new HttpHeaderMatch(context);
        HttpBodyMatch httpBodyMatch = new HttpBodyMatch(context);
        context.setHttpHeaderMatch(httpHeaderMatch);
        context.setHttpBodyMatch(httpBodyMatch);
        bis.setMatch(httpHeaderMatch);

        WriteFinish writeFinish = new WriteFinish(context);
        context.setWriteFinish(writeFinish);
        bos.setWriteFinish(writeFinish);
    }

    @Override
    public void readData(SelectionKey key) throws IOException {
        context.setKey(key);
        attempt(() -> {
            if (bis.readFully() == -1) {
                register.cancel(sc);
            }
        }, key);
    }

    @Override
    public void writeData(SelectionKey key) throws IOException {
        attempt(bos::writeFully, key);
    }

    private void attempt(Attempt attempt, SelectionKey key) throws IOException {
        try {
            attempt.attempt();
        } catch (HttpException e) {
            if (e.isImmediately()) {  // 立即关闭连接
                register.cancel(sc);
                return;
            }
            Response response = getResponse(context);
            // 设置错误信息
            if (e.getBody() != null) {
                response.setStatus(e.getStatus());
                response.setBody(e.getBody());
            } else
                response.setStatusWithBody(e.getStatus());
            response.write(bos);
            // 发送消息
            key.interestOps(SelectionKey.OP_WRITE);
            context.getWriteFinish().setClose(e.isClose());
        } catch (CancelledKeyException | IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Response response = getResponse(context);
            response.setStatusWithBody(HttpState.INTERNAL_SERVER_ERROR);
            response.write(bos);
            key.interestOps(SelectionKey.OP_WRITE);
            context.getWriteFinish().setClose(true);
        }
    }

    private Response getResponse(HttpContext context){
        Response response = context.getResponse();
        if (response == null) {
            response = new Response(context.getResponseBody());
            context.setResponse(response);
        }
        return response;
    }

    interface Attempt{
        void attempt() throws IOException;
    }
}
