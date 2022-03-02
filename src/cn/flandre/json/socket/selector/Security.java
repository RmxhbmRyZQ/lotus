package cn.flandre.json.socket.selector;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Security implements Verification {
    private static final LinkedList<Verification> v = new LinkedList<>();

    public static void registerVerification(Verification verification) {
        v.add(verification);
    }

    @Override
    public boolean verify(SocketChannel sc) {
        for (Verification verification : v) {
            if (!verification.verify(sc))
                return false;
        }
        return true;
    }
}
