package cn.flandre.json.http.resolve;

import cn.flandre.json.constant.IOConstant;
import cn.flandre.json.exception.InvalidHttpHeaderException;
import cn.flandre.json.socket.selector.Register;
import cn.flandre.json.socket.stream.Block;
import cn.flandre.json.socket.stream.BlockInputStream;
import cn.flandre.json.socket.stream.BlockOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;

public class HttpHeaderMatch implements Match {
    private final BlockInputStream bis;
    private final Register register;
    private final HttpContext context;
    private final byte[] delimiter = new byte[]{13, 10, 13, 10};  // 普配的数据
    private final BlockOutputStream bos;
    private int len = 0;
    private int delimiterOffset = 0;

    public HttpHeaderMatch(BlockInputStream bis, BlockOutputStream bos, Register register, HttpContext context) {
        this.bis = bis;
        this.register = register;
        this.context = context;
        this.bos = bos;
    }

    @Override
    public void match(int read, Block block, int offset, SelectionKey key) throws IOException {
        // 要是block里面有两个HTTP头怎么办
        int index = indexOf(block.getBytes(), read, offset);
        if (index == -1) {
            len += read;
        } else {
            len += index;
            try {
                resolveRequest(key, read - index);
            } catch (InvalidHttpHeaderException e) {
                register.cancel(key.channel());
                return;
            }
        }
        String html = "<body><form action=\"/\" method=\"post\" enctype=\"multipart/form-data\"><input type=\"file\" name=\"upload\"><input type=\"submit\" name=\"sub\" id=\"\"></form></body>";
        String response = "HTTP/1.1 200 OK\r\nContent-Length: " + html.length() + "\r\n\r\n" + html;
        bos.write(response.getBytes(StandardCharsets.UTF_8));
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private int indexOf(byte[] source, int r, int offset) {
        int off = offset;
        int i, first = delimiter[0];
        if (offset == IOConstant.BLOCK_SIZE) offset = 0;
        int max = r + offset;

        // 如果是上一块的尾部匹配到一半
        if (delimiterOffset != 0) {
            for (i = 0; i < delimiter.length - delimiterOffset; i++) {
                if (source[offset++] != delimiter[delimiterOffset + i]) break;
            }
            if (delimiterOffset + i == delimiter.length) {
                delimiterOffset = 0;
                return offset - off;
            } else {
                delimiterOffset = 0;
            }
        }

        // 暴力匹配
        for (; offset < max; offset++) {
            if (source[offset] != first) {
                while (++offset < max && source[offset] != first) ;
            }

            if (offset < max) {
                offset++;
                // 不需要回退，HTTP头不会出现\r\r\n\r\n这样的情况
                for (delimiterOffset = 1; offset < max && delimiterOffset < delimiter.length &&
                        source[offset] == delimiter[delimiterOffset]; offset++, delimiterOffset++)
                    ;

                if (delimiterOffset == delimiter.length) {
                    delimiterOffset = 0;
                    return offset - off;
                }
            }
            delimiterOffset = 0;
        }
        return -1;
    }

    private void resolveRequest(SelectionKey key, int left) throws IOException {
        InputStreamReader reader = new InputStreamReader(bis);
        char[] chars = new char[len - 4];
        reader.read(chars);
        reader.skip(4);
        String httpHeader = new String(chars);

        Request request = new Request(httpHeader);
        System.out.print(request);
        context.setRequest(request);
        String contentLength = request.getHeader("Content-Length");

        bis.setMatch(new HttpBodyMatch(contentLength != null ? Integer.parseInt(contentLength) : 0, left, bis, register, request));
        len = 0;
    }
}
