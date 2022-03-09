package cn.flandre.lotus.http.web;

import cn.flandre.lotus.HttpApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartData {
    private static final Pattern FILE_PATTERN = Pattern.compile(
            ".*?name=\"(.*?)\".*?filename=\"(.*?)\"\r\nContent-Type: ?(.*)", Pattern.MULTILINE);

    private String key;
    private String value;
    private String type;
    private String filename;
    private FileItem fileItem;

    public MultipartData(String head) {
        if (head.contains("\r\n")) {
            Matcher matcher = FILE_PATTERN.matcher(head);
            if (matcher.find()) {
                key = matcher.group(1);
                filename = matcher.group(2);
                type = matcher.group(3);
            }
        } else {
            int find = head.indexOf("name=\"");
            key = head.substring(find + 6, head.length() - 1);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    /**
     * 拿到对应的值，客户端发来的是文件，拿到的或是null
     */
    public String getValue() {
        return value;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * 获取文件类型，客户端发送的不是文件为null
     */
    public String getType() {
        return type;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    /**
     * 保存上传的文件
     *
     * @param path 文件保存的路径
     */
    public void upload(String path) throws IOException {
        upload(path, filename);
    }

    /**
     * 保存上传的文件
     *
     * @param path     文件保存的路径
     * @param filename 保存的文件名
     */
    public void upload(String path, String filename) throws IOException {
        if (fileItem == null) return;
        File directory = new File(HttpApplication.setting.getDefaultResourcePath() + path);
        File file = new File(path + File.separator + filename);

        if (!directory.exists() || !directory.isDirectory())
            if (!directory.mkdirs())
                throw new IOException("Failed to create directory");

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileItem.content, fileItem.offset, fileItem.length);
        fos.flush();
        fos.close();
    }

    public void upload(File file, String filename) throws IOException {
        if (fileItem == null) return;
        file = new File(file, filename);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileItem.content, fileItem.offset, fileItem.length);
        fos.flush();
        fos.close();
    }

    public void upload(File file) throws IOException {
        upload(file, filename);
    }

    public static class FileItem {
        private final int offset;
        private final int length;
        private final byte[] content;

        public FileItem(int offset, int length, byte[] content) {
            this.offset = offset;
            this.length = length;
            this.content = content;
        }
    }
}
