package cn.flandre.json.controller;

import cn.flandre.json.http.match.HttpContext;

public interface Controller {
    public void controller(HttpContext context);
}
