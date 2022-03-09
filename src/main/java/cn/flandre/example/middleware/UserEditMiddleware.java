package cn.flandre.example.middleware;

import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.middleware.BasePipeline;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class UserEditMiddleware extends BasePipeline {
    @Override
    public boolean get(HttpContext context, Matcher matcher) {
        String group = matcher.group(1);
        if (group == null) {
            return false;
        }
        Database database = context.getDatabase();
        try {
            Map<String, Object> user = new HashMap<>();
            ResultSet query = database.query("SELECT `id`,`super`,`password`,`sex`,`username` FROM `user` WHERE `id`=?", new Object[]{group});
            if (query.next()) {
                user.put("id", query.getInt(1));
                user.put("super", query.getInt(2));
                user.put("password", query.getString(3));
                user.put("sex", query.getInt(4));
                user.put("username", query.getString(5));
                user.put("type", "edit");
                context.getRequest().putExtra("user", user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        try {
            context.getDatabase().update("UPDATE `user` SET `username`=?,`sex`=?,`super`=?,`password`=? WHERE `id`=?",
                    new Object[]{request.getNormalBody("userName"), request.getNormalBody("sex"),
                            request.getNormalBody("super"), request.getNormalBody("password"),
                            request.getNormalBody("id")});
            request.putExtra("operate", true);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.putExtra("operate", false);
        return false;
    }
}
