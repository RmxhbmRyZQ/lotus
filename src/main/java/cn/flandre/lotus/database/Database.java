package cn.flandre.lotus.database;

import cn.flandre.lotus.HttpApplication;
import cn.flandre.lotus.constant.Setting;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 对JDBC的封装，对数据库不是那么熟悉，大致封装一下
 */
public class Database {
    private Connection connection;
    private Statement statement;
    private final String url;
    private final String username;
    private final String password;
    private final Map<String, PreparedStatement> statementMap = new HashMap<>();

    static {
        try {
            Class.forName(HttpApplication.setting.getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Database() {
        Setting setting = HttpApplication.setting;
        url = setting.getString("databaseUri");
        username = setting.getString("databaseUsername");
        password = setting.getString("databasePassword");
    }

    public void ensureConnectionOpen() throws SQLException {
        if (connection == null || connection.isClosed())
            connection = DriverManager.getConnection(url, username, password);
    }

    public Statement getStatement() throws SQLException {
        ensureConnectionOpen();
        if (statement == null || statement.isClosed()) {
            statement = connection.createStatement();
        }
        return statement;
    }

    public PreparedStatement prepareSql(String sql, Object[] strings) throws SQLException {
        PreparedStatement preparedStatement = statementMap.get(sql);

        if (preparedStatement == null || preparedStatement.isClosed()) {
            preparedStatement = connection.prepareStatement(sql);
            statementMap.put(sql, preparedStatement);
        }

        preparedStatement.clearParameters();

        for (int i = 0; i < strings.length; i++) {
            preparedStatement.setObject(i + 1, strings[i]);
        }

        return preparedStatement;
    }

    public ResultSet query(String sql, Object[] strings) throws SQLException {
        if (strings == null || strings.length == 0) {
            return query(sql);
        }

        ensureConnectionOpen();
        PreparedStatement preparedStatement = prepareSql(sql, strings);

        return preparedStatement.executeQuery();
    }

    public ResultSet query(String sql) throws SQLException {
        ensureConnectionOpen();

        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public int insert(String sql, Object[] strings) throws SQLException {
        if (strings == null || strings.length == 0) {
            return insert(sql);
        }

        ensureConnectionOpen();
        PreparedStatement preparedStatement = prepareSql(sql, strings);

        return preparedStatement.executeUpdate();
    }

    public int insert(String sql) throws SQLException {
        ensureConnectionOpen();

        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }

    public boolean delete(String sql, Object[] strings) throws SQLException {
        if (strings == null || strings.length == 0) {
            return delete(sql);
        }

        ensureConnectionOpen();
        PreparedStatement preparedStatement = prepareSql(sql, strings);

        return preparedStatement.execute();
    }

    public boolean delete(String sql) throws SQLException {
        ensureConnectionOpen();

        Statement statement = connection.createStatement();
        return statement.execute(sql);
    }

    public int update(String sql, Object[] strings) throws SQLException {
        return insert(sql, strings);
    }

    public int update(String sql) throws SQLException {
        return insert(sql);
    }

    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void endTransaction() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void closeStatement() throws SQLException {
        statement.close();
    }

    public void closePreparedStatement() throws SQLException {
        for (PreparedStatement preparedStatement : statementMap.values()) {
            preparedStatement.close();
        }
    }

    public void close() throws SQLException {
        closeStatement();
        closePreparedStatement();
        statementMap.clear();
        connection.close();
    }
}
