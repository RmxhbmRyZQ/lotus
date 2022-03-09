package cn.flandre.example.middleware;

import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.session.Session;
import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.middleware.BasePipeline;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;

public class LoginMiddleware extends BasePipeline {
    @Override
    public boolean get(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        Session session = request.getSession(context);

        String id = session.getAttribute("userId");
        if (id != null) {
            request.putExtra("userId", Integer.parseInt(id));
        }else {
            request.putExtra("userId", -1);
        }

        return false;
    }

    @Override
    public boolean post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        Session session = request.getSession(context);

        String id = session.getAttribute("userId");
        if (id != null) {
            request.putExtra("userId", Integer.parseInt(id));
            return false;
        }

        String username = request.getNormalBody("username");
        String password = request.getNormalBody("password");

        if (username == null || password == null) {
            request.putExtra("userId", -1);
            return false;
        }

        int userId = verify(username, password, context);
        request.putExtra("userId", userId);

        if (userId == -1) {
            return false;
        }

        session.setAttribute("userId", String.valueOf(userId));
        session.setAttribute("username", username);
        return false;
    }

    private int verify(String username, String password, HttpContext context) {
        int anInt = -1;
        Database database = context.getDatabase();
        try {
            ResultSet query = database.query("SELECT `id` FROM `user` WHERE `username`=? AND `password`=?",
                    new Object[]{username, password});

            if (query.next()) {
                anInt = query.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anInt;
    }
}
