# 介绍

lotus 是一个基于 Java 的 nio 包的异步非阻塞的 http 框架，代码简单易于阅读，是我的毕业设计

即 Selector + SocketChannel 实现 TCP 连接，然后解析 http 请求，并通过控制器返回 http 响应

我使用一个消息队列来实现线程池，线程池我也写的极为简单，直接创建 CPU 核心数两倍的线程并启动

关于框架的流程图以后有机会一定写

# 配置文件

## 配置类

```java
public class ServerSetting extends Setting {
    public ServerSetting(String s) throws IOException {
        super(s);
    }

    public static void main(String[] args) throws IOException {
        // 启动函数，./server.json是配置文件路径
        HttpApplication.run(new ServerSetting("./server.json"));
    }

    @Override
    public void initPath() {
        // 设置路径匹配器
        PathGroup.addPath("^/favicon.ico$", new FaviconController("/img/favicon.jpg"));
        PathGroup.addPath("^/static/(.+)", new StaticController("/static"));
        PathGroup.addPath(".*", new IntroduceController());
    }
}
```

## 配置文件

```json
// 配置文件直接使用JSON格式
{
  "databaseUri": "jdbc:mysql://localhost:3306/pblog?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=true",
  "databaseUsername": "root",
  "databasePassword": "",
  "useDatabase": true,
  "databaseDriver": "com.mysql.jdbc.Driver",
  "useSession": true,
  "sessionStore": "cache"
}
```

## 配置字段

```java
setting.put("ip", "0.0.0.0");
setting.put("port", 80);
setting.put("maxHttpHead", 8 * 1024);  // 最大请求头
setting.put("maxContent", 1024 * 1024 * 100);  // 最大请求体
setting.put("keepAlive", true);
setting.put("contentType", "text/html; charset=utf-8");
setting.put("contentEncrypt", "identity");  // 压缩方式，仅支持 identity 和 gzip
setting.put("minEncryptLength", 2 * 1024);  // 超过多少才进行压缩
setting.put("maxEncryptLength", 10 * 1024 * 1024);  // 超过多少不再进行压缩
setting.put("useDatabase", false);  // 是否使用数据库
setting.put("useSession", false);  // 是否使用 session
setting.put("sessionStore", "cache");  // session的储存位置，cache 内存，database 数据库
setting.put("sessionExpireTime", 30 * 60 * 1000);  // session超时时间，30min
setting.put("defaultResourcePath", "./src/main/resources");  // 资源存放路径
// 如果useDatabase为true时，下面四个一定要定义
setting.put("databaseDriver", "");
setting.put("databaseUri", "");
setting.put("databaseUsername", "");
setting.put("databasePassword", "");
```

# 控制器

## 控制类

```java
// 写控制器首要继承这个类，需要处理什么请求就重写什么方法
public class BaseController implements Controller {
    /**
     * @param context 上下文对象
     * @param matcher 请求的路径和路径匹配器的路径匹配的结果，用于提取URL中有用的参数
     */
    public void get(HttpContext context, Matcher matcher);

    public void post(HttpContext context, Matcher matcher);

    public void head(HttpContext context, Matcher matcher);

    public void delete(HttpContext context, Matcher matcher);

    public void put(HttpContext context, Matcher matcher);

    /**
     * 页面跳转
     *
     * @param context 上下文
     * @param path 跳转的路径
     * @param permanent true 301, false 302
     */
    protected void redirect(HttpContext context, String path, boolean permanent);

    /**
     * 模板渲染，使用freemarker
     *
     * @param path 模板路径
     * @param filename 模板名称
     * @param model 数据模型
     * @param context 上下文
     */
    protected void render(String path, String filename, Map<Object, Object> model, HttpContext context);
}
```

## HttpContext上下文

本来我只想给Request和Response以及Macher对象到Controller，不过本框架代码简单就给高点自由度

```java
// 一个连接对应一个上下文
public class HttpContext {
    private Request request;  // HTTP请求
    private Response response;  // HTTP响应
    private HttpHeaderMatch httpHeaderMatch;  // 请求头解析
    private HttpBodyMatch httpBodyMatch;  // 请求体解析
    private SelectionKey key;  // SocketChannel
    private WriteFinish writeFinish;  // 响应写完时的回调
    private final BlockInputStream bis;  // 输入流
    private final BlockOutputStream bos;  // 输出流
    private final Register register;  // 注册器，用来关闭连接
    private final BlockOutputStream responseBody;  // 响应体
    private Database database;  // 数据库
}
```

关于Request：

```java
public class Request {
    public String getParam(String key);  // 获取Uri上的键值对
    public String getHeader(String key);  // 获取请求头
    public String getCookie(String key);  // 获取Cookie
    public RequestMethod getMethod();  // 获取请求的方法
    public String getPath();  // 获取请求路径
    public String getProtocol();  // 获取协议
    public String getNormalBody(String key);  // 获取键值对的请求体
    public MultipartData getFileBody(String key);  // 获取文件上传的请求体
    public void putExtra(String key, Object value);  // 设置额外信息
    public Object getExtra(String key);  // 获取额外信息
    public Map<String, Object> getExtras();  // 获取额外的键值对
    public Session getSession(HttpContext context);  // 获取session
}
```

关于Response：

```java
public class Response {
    public void addHead(String key, String value);  // 添加响应头
    public void removeHead(String key);  // 删除响应头
    public String getHead(String key);  // 获取响应头
    public void setCookie(SetCookieItem item);  // 设置cookie
    public void removeCookie(SetCookieItem item);  // 删除cookie
    public void setStatusWithBody(int status);  // 设置状态码以及对应的响应体
    public void setStatus(int status);  // 设置状态码，状态码使用HttpState里面的常量
    public void setJsonBody(String jsonBody);  // 设置json类型的响应体
    public void setTextBody(String textBody);  // 设置文本类型的响应体
    public void setHtmlBody(String htmlBody);  // 设置html类型的响应体
    public void setBody(byte[] body);  // 设置响应体
    public void setBody(String body);  // 设置响应体
    public void setFileBody(File file);  // 设置文件为响应体
    public void setFileBody(String file);  // 设置文件为响应体
}
```

## 模板

模板使用的是 freemarker，有关他的介绍为：http://freemarker.foofun.cn/

下面是简单的使用：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>${name}</h1>
</body>
</html>
```

```java
public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "john");
        render("./template", "index.html", map, context);
    }
}
```

# 中间件

中间件是在controller处理前或处理后进行的回调，我认为用来处理拦截器，或者SQL请求等生成数据的事情

中间件分为：全局中间件，单路径中间件；全局中间件是任何请求都会调用，而单路径中间件只会在对应的路径被请才去才会调用

```java
// 如果要写中间件的话，需要继承这个类，需要处理什么方法就重写什么函数
public class BasePipeline implements Pipeline {
    /**
     * @param context 上下文
     * @param matcher 路径正则匹配
     * @return true 拦截后面的中间件和控制器都不会被调用
     */
    public boolean get(HttpContext context, Matcher matcher);

    public boolean post(HttpContext context, Matcher matcher);

    public boolean head(HttpContext context, Matcher matcher);

    public boolean delete(HttpContext context, Matcher matcher);

    public boolean put(HttpContext context, Matcher matcher);
}
```

## 全局中间件的注册

```java
GlobalMiddlewareBean.addIn(中间件对象);  // 入的中间件注册
GlobalMiddlewareBean.addOut(中间件对象);  // 出的中间件注册
```

## 局部中间件的注册

```java
public class ServerSetting extends Setting {
    public ServerSetting(String s) throws IOException {
        super(s);
    }

    public static void main(String[] args) throws IOException {
        HttpApplication.run(new ServerSetting("./server.json"));
    }

    @Override
    public void initPath() {
        PathGroup.addPath("^/(index)?/?$", new IndexPipeline(), null, new IndexController());  // 入中间件注册
        PathGroup.addPath("^/(index)?/?$", null, new IndexPipeline(), new IndexController());  // 出中间件注册
        List<Pipeline> list = new Arraylise();
        list.add(new IndexPipeline1);
        list.add(new IndexPipeline2);
        PathGroup.addPath("^/(index)?/?$", list, null, new IndexController());  // 多个入中间件注册
    }
}
```

## 例子

```java
public class IndexPipeline extends BasePipeline {
    @Override
    public boolean get(HttpContext context, Matcher matcher) {
        String group = matcher.group(1);  // http://127.0.0.1/article/12
        Database database = context.getDatabase();
        try {
            ResultSet query = database.query("SELECT text FROM article WHERE article_id = ?", new String[]{group});

            if (query.next()) {
                String string = query.getString(1);
                context.getRequest().putExtra("articleText", string);  // 放入extra提供给controller使用
            }

            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

# Cookie

## Cookie

```java
public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        // 获取Cookie
        Request request = context.getRequest();
        String name = request.getCookie("name");
        // 设置Cookie
        Response response = context.getResponse();
        response.setCookie(new SetCookieItem("name", "john"));
    }
}
```

其中SetCookieItem有以下参数

```java
package cn.flandre.lotus.http.web;

import java.util.Date;

public class SetCookieItem {
    private final String key;
    private final String value;
    private Date expires;  // 过期时间:DAY, DD MMM YYYY HH:MM:SS GMT
    private String domain;  // Cookie有效域名
    private String path;  // Cookie有效路径
    private boolean secure;  // 是否仅通过https传回cookie，由于本框架不支持https，所以该字段没用
    private boolean httpOnly;  // 是否仅为http,https使用

    public SetCookieItem(String key, String value, Date expires, String domain, String path, boolean secure, boolean httpOnly);

    public SetCookieItem(String key, String value);

    public SetCookieItem(String key, String value, Date expires);

    public SetCookieItem(String key, String value, String path);
}
```

## Session

session有两个配置：

```json
{  
    "useSession": true,  // 开启session
    "sessionStore": "cache"  // 设置存储方式为内存，还有database方式，存在数据库，当然需要先配置了数据库
}
```

session的使用：

```java
public class IndexController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        // 获取Session
        Request request = context.getRequest();
        Session session = request.getSession(context);
        session.getAttribute("name");
        // 设置Session
        session.setAttribute("name", "john");
    }
}
```

# Database

要使用database首先要写配置文件

```java
{
  "databaseUri": "jdbc:mysql://localhost:3306/pblog?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=true",  // 数据库的url
  "databaseUsername": "root",  // 用户名
  "databasePassword": "",  // 密码
  "useDatabase": true,  // 使用数据库
  "databaseDriver": "com.mysql.jdbc.Driver"  // 使用mysql驱动
}
```

数据库的方法如下：

```java
// 多的Object[] string参数表示会对sql语句设置参数，如query("select * from article where id=?", new Object[]{"1"})
// 其实就是执行 query("select * from article where id=\"`\"")
public class Database {
    public ResultSet query(String sql, Object[] strings);  // 查询数据
    public ResultSet query(String sql);  // 查询数据
    public int insert(String sql, Object[] strings);  // 插入数据
    public int insert(String sql);  // 插入数据
    public boolean delete(String sql, Object[] strings);  // 删除数据
    public boolean delete(String sql);  // 删除数据
    public int update(String sql, Object[] strings);  // 更新数据
    public int update(String sql);  // 更新数据
    public void beginTransaction();  // 开启事务
    public void endTransaction();  // 关闭事务
}
```

# Csrftoken

本框架没有提供csrftoken的接口，但可以自己实现，下面是例子

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action="/csrf" method="post">
    <input type="text" name=name">
    <input type="text" name=age">
    <input type="hidden" name=csrftoken" value="${csrftoken}">
    <input type="submit" name=submit" value=Submit">
</form>
</body>
</html>
```

```java
public class CsrfController extends BaseController {
    @Override
    public void get(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        String token = request.getCookie("csrftoken");
        
        if (token == null) {
            token = String.valueOf(UUID.randomUUID());  // 这里只是为了方便而已
            Response response = context.getResponse();
            response.setCookie(new SetCookieItem("csrftoken", token));
        }
        
        Map<Object, Object> model = new HashMap<>();
        model.put("csrftoken", token);
        render(HttpApplication.setting.getDefaultResourcePath() + "/html", "csrf.html", model, context);
    }

    @Override
    public void post(HttpContext context, Matcher matcher) {
        Request request = context.getRequest();
        System.out.println(request.getNormalBody("name"));
        System.out.println(request.getNormalBody("age"));
        System.out.println(request.getNormalBody("csrftoken"));
        /*
         * john
         * 18
         * 973ab9be-c957-4833-9fbd-e990ea3be953
         */
    }
}
```

