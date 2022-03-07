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
import java.util.zip.GZIPOutputStream;

public class Test {
    public static void main(String[] args) throws IOException {
        long t1, t2;
        t1 = System.currentTimeMillis();
        File file = new File("D:\\BaiduNetdiskDownload\\VMware-workstation-full-16.1.0-17198959.exe");
        int len = (int) file.length();
        byte[] bytes = new byte[len];
        FileChannel fileChannel = new FileInputStream(file).getChannel();
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        FileInputStream fis = new FileInputStream(file);
        BlockOutputStream blockOutputStream = new BlockOutputStream(null, new FreeBlock());
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(blockOutputStream);
        t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

        t1 = System.currentTimeMillis();
        System.out.println(fileChannel.read(wrap));
        t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

        t1 = System.currentTimeMillis();
        System.out.println(fis.read(bytes));
        t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

        t1 = System.currentTimeMillis();
        WritableByteChannel writableByteChannel = Channels.newChannel(new BlockOutputStream(null, new FreeBlock()));
        FileChannel channel = new FileInputStream(file).getChannel();
        channel.transferTo(0, len, writableByteChannel);
        t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

//        t1 = System.currentTimeMillis();
//        gzipOutputStream.write(bytes);
//        gzipOutputStream.finish();
//        System.out.println(blockOutputStream.size());
//        t2 = System.currentTimeMillis();
//        System.out.println(t2 - t1);

        int breakPoint = 0;
    }
}
