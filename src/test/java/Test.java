import cn.flandre.lotus.socket.stream.BlockOutputStream;
import cn.flandre.lotus.socket.stream.FreeBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class Test {
    public static void main(String[] args) throws IOException {
        String toekn = String.valueOf(UUID.randomUUID());
        int breakPoint = 0;
    }
}