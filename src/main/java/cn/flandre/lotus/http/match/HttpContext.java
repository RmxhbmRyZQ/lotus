package cn.flandre.lotus.http.match;

import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.http.web.Response;
import cn.flandre.lotus.socket.selector.Register;
import cn.flandre.lotus.socket.stream.BlockInputStream;
import cn.flandre.lotus.socket.stream.BlockOutputStream;

import java.nio.channels.SelectionKey;

/**
 * 一个连接中使用的上下文
 */
public class HttpContext {
    private Request request;
    private Response response;
    private HttpHeaderMatch httpHeaderMatch;
    private HttpBodyMatch httpBodyMatch;
    private SelectionKey key;
    private WriteFinish writeFinish;
    private final BlockInputStream bis;
    private final BlockOutputStream bos;
    private final Register register;
    private final BlockOutputStream responseBody;

    public HttpContext(BlockInputStream bis, BlockOutputStream bos, Register register, BlockOutputStream body) {
        this.bis = bis;
        this.bos = bos;
        this.register = register;
        responseBody = body;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public void setWriteFinish(WriteFinish writeFinish) {
        this.writeFinish = writeFinish;
    }

    public WriteFinish getWriteFinish() {
        return writeFinish;
    }

    public void setHttpBodyMatch(HttpBodyMatch httpBodyMatch) {
        this.httpBodyMatch = httpBodyMatch;
    }

    public void setHttpHeaderMatch(HttpHeaderMatch httpHeaderMatch) {
        this.httpHeaderMatch = httpHeaderMatch;
    }

    public HttpBodyMatch getHttpBodyMatch() {
        return httpBodyMatch;
    }

    public HttpHeaderMatch getHttpHeaderMatch() {
        return httpHeaderMatch;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Register getRegister() {
        return register;
    }

    public BlockOutputStream getBos() {
        return bos;
    }

    public BlockInputStream getBis() {
        return bis;
    }

    public BlockOutputStream getResponseBody() {
        return responseBody;
    }
}
