package cn.flandre.json.http.resolve;

import cn.flandre.json.socket.selector.Register;
import cn.flandre.json.socket.stream.Block;
import cn.flandre.json.socket.stream.BlockInputStream;

import java.nio.channels.SelectionKey;

public class HttpBodyMatch implements Match {
    public HttpBodyMatch(int require, int len, BlockInputStream bis, Register register, Request request) {

    }

    @Override
    public void match(int read, Block block, int offset, SelectionKey key) {

    }
}
