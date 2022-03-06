package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.http.match.HttpBodyMatch;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.match.HttpHeaderMatch;
import cn.flandre.lotus.http.match.WriteFinish;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.socket.stream.BlockInputStream;
import cn.flandre.lotus.socket.stream.BlockOutputStream;
import cn.flandre.lotus.socket.stream.FreeBlock;
import cn.flandre.lotus.socket.stream.SocksOutputStream;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 连接的封装处理，由于多路复用的关系，不需要进行线程同步的处理
 */
public class Client extends AbstractSelect {
    private final SocketChannel sc;
    private final Register register;
    private final BlockInputStream bis;
    private final BlockOutputStream bos;
    private final HttpContext context;

    public Client(SocketChannel sc, Register register, FreeBlock freeBlock) {
        this.sc = sc;
        this.register = register;
        SocksOutputStream sos = new SocksOutputStream(sc);
        bis = new BlockInputStream(sc, freeBlock);
        bos = new BlockOutputStream(sos, freeBlock);

        context = new HttpContext(bis, bos, register, new BlockOutputStream(sos, freeBlock));
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
    public void onRead(SelectionKey key) throws IOException {
        context.setKey(key);
        try {
            if (bis.readFully() == -1) {
                register.cancel(sc);
            }
        } catch (HttpException e) {
            if (e.isImmediately()) {  // 立即关闭连接
                register.cancel(sc);
                return;
            }
            Response response = context.getResponse();
            if (response == null)
                response = new Response(context.getResponseBody());
            if (e.getBody() != null) {
                response.setStatus(e.getStatus());
                response.setBody(e.getBody());
            } else
                response.setStatusWithBody(e.getStatus());
            response.write(bos);
            key.interestOps(SelectionKey.OP_WRITE);
            context.getWriteFinish().setClose(e.isClose());
        }
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        bos.writeFully();
    }
}
