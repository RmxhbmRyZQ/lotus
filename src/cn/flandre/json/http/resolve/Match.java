package cn.flandre.json.http.resolve;

import cn.flandre.json.socket.stream.Block;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Match {
    void match(int read, Block block, int offset, SelectionKey key) throws IOException;
}
