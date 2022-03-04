package cn.flandre.json.http.match;

import cn.flandre.json.constant.IOConstant;
import cn.flandre.json.exception.InvalidHttpHeaderException;
import cn.flandre.json.http.web.Request;
import cn.flandre.json.http.web.Response;
import cn.flandre.json.middleware.GlobalMiddleware;
import cn.flandre.json.middleware.Pipeline;
import cn.flandre.json.socket.stream.Block;

public class HttpHeaderMatch implements Match {
    private final HttpContext context;
    private final byte[] delimiter = new byte[]{13, 10, 13, 10};  // 普配的数据
    private int len = 0;
    private int delimiterOffset = 0;

    public HttpHeaderMatch(HttpContext context) {
        this.context = context;
    }

    @Override
    public void match(int read, Block block, int offset) {
        HttpBodyMatch match = context.getHttpBodyMatch();
        // 要是block里面有两个HTTP头怎么办
        int index = indexOf(block.getBytes(), read, offset);
        if (index == -1) {
            len += read;
        } else {
            len += index;
            try {
                Request request = resolveRequest();

                context.setRequest(request);
                context.setResponse(new Response());

                for (Pipeline pipeline : GlobalMiddleware.in) {
                    if (!pipeline.handle(context)) {
                        return;
                    }
                }

                String contentLength = request.getHeader("Content-Length");
                match.setRequire(contentLength != null ? Integer.parseInt(contentLength) : 0);
                match.match(read - index, block, offset + index);
                context.getBis().setMatch(match);
            } catch (InvalidHttpHeaderException e) {
                context.getRegister().cancel(context.getKey().channel());
            }
        }
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

    private final byte[] skip = new byte[4];

    private Request resolveRequest() throws InvalidHttpHeaderException {
        byte[] bytes = new byte[len - 4];
        context.getBis().read(bytes);
        context.getBis().read(skip);
        String httpHeader = new String(bytes);

        Request request = new Request(httpHeader);
        len = 0;

        return request;
    }
}
