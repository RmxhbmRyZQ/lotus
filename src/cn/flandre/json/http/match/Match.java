package cn.flandre.json.http.match;

import cn.flandre.json.socket.stream.Block;

import java.io.IOException;

public interface Match {
    void match(int read, Block block, int offset) throws IOException;
}
