package cn.flandre.lotus.http.match;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.exception.HttpException;
import cn.flandre.lotus.constant.HttpState;
import cn.flandre.lotus.constant.IOConstant;
import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.http.web.RequestMethod;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.socket.stream.Block;

import java.util.Date;

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
        if (len + read > HttpApplication.setting.getMaxHttpHead())  // 请求头太大了
            throw new HttpException(HttpState.BAD_REQUEST);

        HttpBodyMatch match = context.getHttpBodyMatch();
        int index = indexOf(block.getBytes(), read, offset);
        if (index == -1) {
            len += read;
        } else {
            len += index;
            Request request = resolveRequest();
            Response response = getResponse(request);

            context.setRequest(request);
            context.setResponse(response);
            context.getResponse().setEncrypt(request.getHeader("Accept-Encoding"));

            String contentLength = request.getHeader("Content-Length");
            int length = contentLength != null ? Integer.parseInt(contentLength) : 0;

            // 只有 POST 和 PUT 才会带有请求体
            if (length != 0 && request.getMethod() != RequestMethod.POST && request.getMethod() != RequestMethod.PUT) {
                throw new HttpException(HttpState.BAD_REQUEST);
            }

            // 请求体太大了
            if (length >= HttpApplication.setting.getMaxContent()) {
                throw new HttpException(HttpState.REQUEST_ENTITY_TOO_LARGE);
            }

            // 更改匹配为请求体匹配
            match.setRequire(length);
            match.match(read - index, block, offset + index);
            context.getBis().setMatch(match);
        }
    }

    private Response getResponse(Request request) {
        Response response = new Response(context.getResponseBody());
        Setting setting = HttpApplication.setting;
        response.addHead("Server", "lotus");
        response.addHead("Connection", setting.isKeepAlive() ? "keep-alive" : "close");
        response.addHead("Date", new Date().toString());
        response.addHead("Content-Type", setting.getContentType());
        response.addHead("X-Powered-By", "Java/1.8");
        return response;
    }

    private int indexOf(byte[] source, int r, int offset) {
        int off = offset;
        int i;
        byte first = delimiter[0];
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

    private Request resolveRequest() {
        byte[] bytes = new byte[len - 4];
        context.getBis().read(bytes);
        context.getBis().read(skip);
        String httpHeader = new String(bytes);
        if (httpHeader.length() > HttpApplication.setting.getMaxHttpHead()) {
            throw new HttpException(HttpState.REQUEST_URI_TOO_LONG);
        }

        Request request = new Request(httpHeader);
        len = 0;

        return request;
    }
}
