package cn.flandre.json.http.web;

public enum RequestMethod {
    GET, POST, IGNORANT_METHOD;

    public static RequestMethod parseString(String method) {
        switch (method.toUpperCase()){
            case "GET":
                return GET;
            case "POST":
                return POST;
            default:
                return IGNORANT_METHOD;
        }
    }
}
