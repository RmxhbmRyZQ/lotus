package example.controller;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.database.Database;
import cn.flandre.lotus.http.match.HttpContext;
import cn.flandre.lotus.http.session.Session;
import cn.flandre.lotus.http.web.Request;

import java.sql.*;
import java.util.regex.Matcher;

public class UserController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        Session session = request.getSession(context);
        System.out.println(session.getAttribute("user-id"));
        System.out.println(session.getAttribute("name"));
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
                Session session = context.getRequest().getSession(context);
                session.setAttribute("user-id", query.getString(1));
                session.setAttribute("name", "john");
            }

            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        redirect(context, "/user/", false);
    }
}
