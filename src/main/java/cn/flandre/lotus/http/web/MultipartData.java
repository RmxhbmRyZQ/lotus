package cn.flandre.lotus.http.web;

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

    public String getValue() {
        return value;
    }

    public String getFilename() {
        return filename;
    }

    public String getType() {
        return type;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public void upload(String path) throws IOException {
        upload(path, filename);
    }

    public void upload(String path, String filename) throws IOException {
        if (fileItem == null) return;
        File directory = new File(path);
        File file = new File(path + File.separator + filename);

        if (!directory.exists() || !directory.isDirectory())
            if (!directory.mkdirs())
                throw new IOException("Failed to create directory");

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileItem.content, fileItem.offset, fileItem.length);
        fos.flush();
        fos.close();
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
