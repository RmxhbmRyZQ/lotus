package cn.flandre.lotus.http.match;

import cn.flandre.lotus.socket.stream.Block;

import java.io.IOException;

public interface Match {
    /**
     * @param read 有多少新数据
     * @param block 数据
     * @param offset 偏移
     */
    void match(int read, Block block, int offset) throws IOException;
}
