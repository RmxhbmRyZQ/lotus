package cn.flandre.lotus.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Cursor {
    private final ResultSet resultSet;

    public Cursor(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public boolean moveToNext() throws SQLException {
        return resultSet.next();
    }

    public boolean moveToFirst() throws SQLException {
        return resultSet.first();
    }

    public void close() throws SQLException {
        resultSet.close();
    }
}
