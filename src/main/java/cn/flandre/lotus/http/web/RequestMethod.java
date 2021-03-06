package cn.flandre.lotus.http.web;

public enum RequestMethod {
    GET, POST, HEAD, DELETE, PUT, IGNORANT_METHOD;

    public static RequestMethod parseString(String method) {
        switch (method.toUpperCase()){
            case "GET":
                return GET;
            case "POST":
                return POST;
            case "HEAD":
                return HEAD;
            case "PUT":
                return PUT;
            case "DELETE":
                return DELETE;
            default:
                return IGNORANT_METHOD;  // 请求方法未知
        }
    }
}
