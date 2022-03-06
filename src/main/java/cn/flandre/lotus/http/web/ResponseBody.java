package cn.flandre.lotus.http.web;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.exception.ResponseBodyAlreadySetException;
import cn.flandre.lotus.socket.stream.BlockOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class ResponseBody {
    private final Response response;
    /**
     * setting 使用 gzip 压缩时，小文件使用压缩，大文件使用 transferTo
     * 如果不压缩，直接全部使用 transferTo
     */
    private File fileBody;
    private long fileLength;
    private long transfer;
    private FileChannel fileChannel;
    private byte[] body;
    private boolean encrypt = false;

    public ResponseBody(Response response) {
        this.response = response;
    }

    public void setBody(byte[] body) {
        if (fileBody != null)
            throw new ResponseBodyAlreadySetException("Cannot set body, when the filebody have been set");

        Setting setting = HttpApplication.setting;
        if (!encrypt || body.length < setting.getMinEncryptLength() || body.length > setting.getMaxEncryptLength()){
            response.addHead("Content-Length", String.valueOf(body.length));
            this.body = body;
        }

        switch (setting.getContentEncrypt()){
            case "gzip":
                // 以后一定写
                throw new RuntimeException("The code is waiting to write");
            case "identity":
            default:
                response.addHead("Content-Length", String.valueOf(body.length));
                this.body = body;
                break;
        }
    }

    public void setFileBody(File file) throws FileNotFoundException {
        if (body != null)
            throw new ResponseBodyAlreadySetException("Cannot set filebody, when the body have been set");
        if (file == null || !file.exists())
            throw new FileNotFoundException();
        Setting setting = HttpApplication.setting;
        long length = file.length();

        if (!encrypt || length > setting.getMaxEncryptLength() || length < setting.getMinEncryptLength()) {
            fileBody = file;
            fileLength = length;
            response.addHead("Content-Length", String.valueOf(fileLength));
            return;
        }

        switch (setting.getContentEncrypt()) {
            case "gzip":
                // 以后一定写
                throw new RuntimeException("The code is waiting to write");
            case "identity":
            default:
                fileBody = file;
                fileLength = length;
                response.addHead("Content-Length", String.valueOf(fileLength));
                break;
        }
    }

    public boolean shouldTransferTo() {
        if (fileBody != null) {
            try {
                fileChannel = new FileInputStream(fileBody).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean transfer(WritableByteChannel channel) throws IOException {
        // 想使用 zip 压缩，但又不能进行 transfer 了
        transfer += fileChannel.transferTo(transfer, fileLength - transfer, channel);
        boolean finish = transfer == fileLength;
        if (finish) fileChannel.close();
        return finish;
    }

    public boolean shouldWriteBody() {
        return body != null;
    }

    public void writeBody(BlockOutputStream bos) {
        bos.write(body);
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
}
