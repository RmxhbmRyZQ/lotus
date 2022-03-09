package cn.flandre.example.middleware;

import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.middleware.BasePipeline;

import java.sql.SQLException;
import java.util.regex.Matcher;

public class UserDeleteMiddleware extends BasePipeline {
    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        String group = matcher.group(1);
        try {
            context.getDatabase().delete("DELETE FROM `user` WHERE `id`=?", new Object[]{group});
            context.getRequest().putExtra("operate", true);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        context.getRequest().putExtra("operate", false);
        return false;
    }
}
