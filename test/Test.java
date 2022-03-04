import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static final Object lock = new Object();

    public static void main(String[] args) throws IOException {
//        FileOutputStream fileOutputStream = new FileOutputStream("./a.data");
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0;i<500000;i++){
//            builder.append(i).append(" ");
//        }
//        fileOutputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        String uri = "/index/5675/9908/book.html";
        Pattern compile = Pattern.compile("/index/(\\d+)/(?<bookId>\\d+)/.*");
        Matcher matcher = compile.matcher(uri);
        matcher.find();
        System.out.println(matcher.group("bookId"));
        System.out.println(matcher.groupCount());
        for (int i=0;i<=matcher.groupCount();i++){
            System.out.println(matcher.group(i));
        }
        int breakPoint = 0;
    }
}
