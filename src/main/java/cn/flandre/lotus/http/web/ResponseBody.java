package cn.flandre.lotus.http.web;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;
import cn.flandre.lotus.exception.ResponseBodyAlreadySetException;
import cn.flandre.lotus.socket.stream.BlockOutputStream;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class ResponseBody {
    /**
     * setting 使用 gzip 压缩时，小文件使用压缩，大文件使用 transferTo
     * 如果不压缩，直接全部使用 transferTo
     */
    private File fileBody;
    private long fileLength;
    private long transfer;
    private FileChannel fileChannel;
    private final Response response;
    private final BlockOutputStream body;
    private boolean encrypt = false;

    public ResponseBody(Response response, BlockOutputStream body) {
        this.response = response;
        this.body = body;
    }

//    private void writeGZIP(byte[] bytes) throws IOException {
//        GZIPOutputStream gos = new GZIPOutputStream(body);
//        gos.write(bytes);
//        gos.finish();
//        response.addHead("Content-Encoding", "gzip");
//    }

    public void setBody(byte[] body) {
        if (fileBody != null)
            throw new ResponseBodyAlreadySetException("Cannot set body, when the filebody have been set");
        OutputStream os = getOS(body.length);
        try {
            os.write(body);
            finish(os);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            this.body.write(body);
            response.removeHead("Content-Encoding");
        }
//        Setting setting = HttpApplication.setting;
//        if (!encrypt || body.length < setting.getMinEncryptLength() || body.length > setting.getMaxEncryptLength()) {
//            this.body.write(body);
//            return;
//        }
//
//        switch (setting.getContentEncrypt()) {
//            case "gzip":
//                try {
//                    writeGZIP(body);
//                    break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            case "identity":
//            default:
//                this.body.write(body);
//                break;
//        }
    }

    public void finish(OutputStream os) throws IOException {
        if (os instanceof GZIPOutputStream) {
            ((GZIPOutputStream) os).finish();
        }
    }

    public OutputStream getOS(String path, String filename) {
        File file = new File(path + File.separator + filename);
        long len = file.length();
        return getOS(len);
    }

    public OutputStream getOS(long len) {
        Setting setting = HttpApplication.setting;

        if (!encrypt || len < setting.getMinEncryptLength() || len > setting.getMaxEncryptLength()) {
            return body;
        }

        switch (setting.getContentEncrypt()) {
            case "gzip":
                try {
                    OutputStream os = new GZIPOutputStream(body);
                    response.addHead("Content-Encoding", "gzip");
                    return os;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            case "identity":
            default:
                return body;
        }
    }

    public void setFileBody(File file) throws FileNotFoundException {
        if (body.available())
            throw new ResponseBodyAlreadySetException("Cannot set filebody, when the body have been set");
        if (file == null || !file.exists())
            throw new FileNotFoundException();
        long length = file.length();

        OutputStream os = getOS(length);
        if (os == body) {
            fileBody = file;
            fileLength = length;
            return;
        }

        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        try {
            fis.read(bytes);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            fileBody = file;
            fileLength = length;
            response.removeHead("Content-Encoding");
            return;
        }

        try {
            os.write(bytes);
            finish(os);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            body.write(bytes);
            response.removeHead("Content-Encoding");
        }
//        Setting setting = HttpApplication.setting;
//        if (!encrypt || length > setting.getMaxEncryptLength() || length < setting.getMinEncryptLength()) {
//            fileBody = file;
//            fileLength = length;
//            return;
//        }
//
//        switch (setting.getContentEncrypt()) {
//            case "gzip":
//                try {
//                    byte[] bytes = new byte[(int) file.length()];
//                    FileInputStream fis = new FileInputStream(file);
//                    fis.read(bytes);
//                    writeGZIP(bytes);
//                    break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            case "identity":
//            default:
//                fileBody = file;
//                fileLength = length;
//                break;
//        }
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
        transfer += fileChannel.transferTo(transfer, fileLength - transfer, channel);
        boolean finish = transfer == fileLength;
        if (finish) fileChannel.close();
        return finish;
    }

    public boolean shouldWriteBody() {
        return body.available();
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public long length() {
        if (body.size() > 0)
            return body.size();
        return fileLength;
    }
}
