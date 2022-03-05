package cn.flandre.lotus.http.match;

import cn.flandre.lotus.socket.stream.Block;

import java.io.IOException;

public interface Match {
    void match(int read, Block block, int offset) throws IOException;
}
