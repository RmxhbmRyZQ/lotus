package cn.flandre.lotus.socket.selector;

import cn.flandre.lotus.http.match.HttpBodyMatch;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.match.HttpHeaderMatch;
import cn.flandre.lotus.http.match.WriteFinish;
import cn.flandre.lotus.socket.stream.BlockInputStream;
import cn.flandre.lotus.socket.stream.BlockOutputStream;
import cn.flandre.lotus.socket.stream.FreeBlock;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client extends AbstractSelect {
    private final SocketChannel sc;
    private final Register register;
    private final BlockInputStream bis;
    private final BlockOutputStream bos;
    private HttpContext context;

    public Client(SocketChannel sc, Register register, FreeBlock freeBlock) {
        this.sc = sc;
        this.register = register;
        bis = new BlockInputStream(sc, freeBlock);
        bos = new BlockOutputStream(sc, freeBlock);
        initMatch();
    }

    private void initMatch() {
        context = new HttpContext(bis, bos, register);
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
        if (bis.readFully() == -1) {
            register.cancel(sc);
        }
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        bos.writeFully();
    }
}
