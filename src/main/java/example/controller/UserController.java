package example.controller;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.database.Cursor;
import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.web.Request;
import cn.flandre.lotus.http.web.SetCookieItem;

import java.sql.*;
import java.util.regex.Matcher;

public class UserController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        render("./template", "user.html", null, context);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        String username = request.getNormalBody("username");
        String password = request.getNormalBody("password");

        Database database = context.getDatabase();
        try {
            ResultSet query = database.query("SELECT admin_id FROM p_admin WHERE admin_name=? AND admin_pwd=?", new String[]{username, password});

            if (query.next()) {
                context.getResponse().setCookie(new SetCookieItem("user-id", query.getString(1)));
            }

            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        redirect(context, "/user/", false);

    }
}
