package cn.flandre.lotus.http.session;

import cn.flandre.lotus.HttpApplication;

import java.util.*;

public class MemorySession implements Session {
    private static final LinkedHashMap<String, MemorySession> sessions = new LinkedHashMap<>(16, 0.75f, true);
    private static final LinkedList<MemorySession> list = new LinkedList<>();
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static Session getSession(String id) {
//        System.out.println(id);
        MemorySession session = null;
        if (id != null) {
            synchronized (lock1) {
                session = sessions.get(id);
            }
        }

        if (session == null) {
            session = new MemorySession();
            synchronized (lock1) {
                synchronized (lock2) {
                    sessions.put(session.getID(), session);
                }
            }
        } else {
            session.expireTime = System.currentTimeMillis() + HttpApplication.setting.getSessionExpireTime();
        }

        return session;
    }

    private final String id;
    private final Map<String, String> kv = new HashMap<>();
    private long expireTime;

    public MemorySession() {
        this.id = String.valueOf(UUID.randomUUID());
        expireTime = System.currentTimeMillis() + HttpApplication.setting.getSessionExpireTime();
    }

    @Override
    public String getAttribute(String key) {
        return kv.get(key);
    }

    @Override
    public void setAttribute(String key, String value) {
        kv.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        sessions.remove(key);
    }

    @Override
    public boolean updateAttribute() {
        return true;
    }

    @Override
    public boolean refresh() {
        long time = System.currentTimeMillis();
        synchronized (lock2) {
            for (Map.Entry<String, MemorySession> entry : sessions.entrySet()) {
                if (entry.getValue().expireTime <= time) list.add(entry.getValue());
                else break;
            }
        }
        synchronized (lock1) {
            synchronized (lock2) {
                for (MemorySession session : list) {
                    sessions.remove(session.getID());
                }
            }
        }
        return true;
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public String getID() {
        return id;
    }
}
