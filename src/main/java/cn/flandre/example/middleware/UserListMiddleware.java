package cn.flandre.example.middleware;

import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.json.JSONArray;
import cn.flandre.lotus.json.JSONObject;
import cn.flandre.lotus.middleware.BasePipeline;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;

public class UserListMiddleware extends BasePipeline {
    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        Database database = context.getDatabase();
        try {
            JSONArray a = new JSONArray();
            ResultSet query = database.query("SELECT `id`,`super`,`password`,`sex`,`username` FROM `user`");
            while (query.next()) {
                JSONObject user = new JSONObject();
                user.put("id", query.getInt(1));
                user.put("super", query.getInt(2));
                user.put("password", query.getString(3));
                user.put("sex", query.getInt(4));
                user.put("username", query.getString(5));
                a.put(user);
            }
            JSONObject o = new JSONObject();
            o.put("content", a);
            context.getRequest().putExtra("userlist", o);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
