import java.io.*;
import java.nio.charset.StandardCharsets;
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

        int i = indexOf("djwia123ljdaslidjwlia123".getBytes(StandardCharsets.UTF_8), 0, "123".getBytes(StandardCharsets.UTF_8));
        System.out.println(i);
        System.out.println("djwialjdaslidjwlia123".substring(i));
    }

    private static int indexOf(byte[] src, int offset, byte[] match) {
        byte first = match[0];
        int max = src.length - match.length;
        for (; offset <= max; offset++) {
            if (src[offset] != first) {
                while (++offset < max && src[offset] != first) ;
            }
            /* Found first character, now look at the rest of v2 */
            if (offset <= max) {
                int j = offset + 1;
                int end = j + match.length - 1;
                for (int k = 1; j < end && src[j] == match[k]; j++, k++) ;

                if (j == end) {
                    /* Found whole string. */
                    return j;
                }
            }
        }
        return -1;
    }
}
