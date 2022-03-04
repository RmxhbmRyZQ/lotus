package cn.flandre.json.socket.selector;

import cn.flandre.json.http.resolve.HttpContext;
import cn.flandre.json.http.resolve.HttpHeaderMatch;
import cn.flandre.json.socket.stream.BlockInputStream;
import cn.flandre.json.socket.stream.BlockOutputStream;
import cn.flandre.json.socket.stream.FreeBlock;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client extends AbstractSelect {
    private final SocketChannel sc;
    private final Register register;
    private final BlockInputStream bis;
    private final BlockOutputStream bos;
    private final HttpContext context = new HttpContext();

    public Client(SocketChannel sc, Register register, FreeBlock freeBlock) {
        this.sc = sc;
        this.register = register;
        bis = new BlockInputStream(sc, freeBlock);
        bos = new BlockOutputStream(sc, freeBlock);
        bis.setMatch(new HttpHeaderMatch(bis, bos, register, context));
    }

    @Override
    public void onRead(SelectionKey key) throws IOException {
        if (bis.readFully(key) == -1) {
            register.cancel(sc);
        }
    }

    @Override
    public void onWrite(SelectionKey key) throws IOException {
        if (bos.writeFully()) {
            if (context.getRequest().getHeader("Connection").equalsIgnoreCase("keep-live"))
                key.interestOps(SelectionKey.OP_READ);
            else register.cancel(key.channel());
        }
    }
}
