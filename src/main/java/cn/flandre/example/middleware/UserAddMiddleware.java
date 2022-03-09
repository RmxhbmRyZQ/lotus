package cn.flandre.example.middleware;

import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.middleware.BasePipeline;

import java.sql.SQLException;
import java.util.regex.Matcher;

public class UserAddMiddleware extends BasePipeline {
    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        try {
            context.getDatabase().insert("INSERT INTO `user` (`username`, `sex`, `super`, `password`)values(?,?,?,?)",
                    new Object[]{request.getNormalBody("userName"), Integer.parseInt(request.getNormalBody("sex")),
                            Integer.parseInt(request.getNormalBody("super")), request.getNormalBody("password")});
            request.putExtra("operate", true);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.putExtra("operate", false);
        return false;
    }
}
