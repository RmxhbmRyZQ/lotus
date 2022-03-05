package cn.flandre.lotus.socket.stream;

import java.util.LinkedList;

import static cn.flandre.lotus.constant.IOConstant.BLOCK_SIZE;

public class FreeBlock {

    private final LinkedList<Block> free = new LinkedList<>();  // 全局空闲块内存

    /**
     * 从全局空闲块申请一个块
     */
    public Block poll() {
        Block block;
        synchronized (this) {
            block = free.poll();
        }
        if (block == null) {
            block = new Block(new byte[BLOCK_SIZE], 0, 0);
        } else {
            block.reset();
        }
        return block;
    }

    /**
     * 添加一个块到全局空闲块中
     */
    public void add(Block block) {
        synchronized (this) {
            free.add(block);
        }
    }
}
