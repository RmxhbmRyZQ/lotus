package cn.flandre.lotus.http.session;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.exception.UnsupportedDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DatabaseSession implements Session {
    private static final String TABLE = "CREATE TABLE IF NOT EXISTS `lotus_session` (" +
            "    `key` varchar(40) NOT NULL PRIMARY KEY," +
            "    `data` text NOT NULL," +
            "    `expire_time` bigint unsigned NOT NULL" +
            ");";
    private static final String QUERY = "SELECT data FROM lotus_session WHERE `key`=?";
    private static final String UPDATE = "UPDATE lotus_session SET `data`=?, `expire_time`=? WHERE `key`=?";
    private static final String INSERT = "INSERT INTO lotus_session (`key`, `data`, `expire_time`) VALUES (?, ?, ?)";
    private static final String DELETE = "DELETE FROM lotus_session WHERE `expire_time` <= ?";

    static {
        if (HttpApplication.setting.getSessionStore().equalsIgnoreCase("database")) {
            Database database = new Database();
            try {
                database.getStatement().execute(TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private final Database database;
    private final String id;
    private boolean nullId;
    private long expireTime;
    private final Map<String, String> sessions = new HashMap<>();

    public DatabaseSession(Database database, String id) {
        if (!HttpApplication.setting.getSessionStore().equalsIgnoreCase("database")) {
            throw new UnsupportedDatabaseException();
        }
        this.database = database;
        nullId = id == null;

        if (!nullId) {
            ResultSet query;
            try {
                query = database.query(QUERY, new Object[]{id});
                nullId = !query.next();
                if (!nullId) {
                    String string = query.getString(1);
                    if (!string.equals("")) {
                        String[] split = string.split("\0");
                        for (String s : split) {
                            String[] kv = s.split("\1");
                            sessions.put(kv[0], kv[1]);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                nullId = true;
            }
        }

        if (nullId) {
            id = String.valueOf(UUID.randomUUID());
        }

        this.id = id;
    }

    @Override
    public String getAttribute(String key) {
        return sessions.get(key);
    }

    @Override
    public void setAttribute(String key, String value) {
        sessions.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        sessions.remove(key);
    }

    @Override
    public boolean updateAttribute() {
        expireTime = System.currentTimeMillis() + HttpApplication.setting.getSessionExpireTime();
        String data = produceData();
        try {
            if (nullId) {
                database.insert(INSERT, new Object[]{id, data, expireTime});
            } else {
                database.update(UPDATE, new Object[]{data, expireTime, id});
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String produceData() {
        if (sessions.size() == 0) return "";

        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = sessions.entrySet().iterator();
        Map.Entry<String, String> next = iterator.next();
        builder.append(next.getKey()).append("\1").append(next.getValue());

        while (iterator.hasNext()) {
            next = iterator.next();
            builder.append("\0").append(next.getKey()).append("\1").append(next.getValue());
        }

        return builder.toString();
    }

    @Override
    public boolean refresh() {
        try {
            database.delete(DELETE, new Object[]{System.currentTimeMillis()});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
