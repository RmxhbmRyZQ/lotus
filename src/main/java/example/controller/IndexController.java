package example.controller;

import cn.flandre.lotus.controller.BaseController;
import cn.flandre.lotus.http.match.HttpContext;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "john");
        render("./template", "index.html", map, context);
        
    }
}
