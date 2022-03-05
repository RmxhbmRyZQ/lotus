package cn.flandre.json.http.match;

import cn.flandre.json.http.web.Request;
import cn.flandre.json.http.web.Response;
import cn.flandre.json.socket.selector.Register;
import cn.flandre.json.socket.stream.BlockInputStream;
import cn.flandre.json.socket.stream.BlockOutputStream;

import java.nio.channels.SelectionKey;

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

    public HttpContext(BlockInputStream bis, BlockOutputStream bos, Register register) {
        this.bis = bis;
        this.bos = bos;
        this.register = register;
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
}
