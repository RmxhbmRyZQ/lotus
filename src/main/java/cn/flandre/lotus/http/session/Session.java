package cn.flandre.lotus.http.session;

public interface Session {
    public String getAttribute(String key);

    public void setAttribute(String key, String value);
}
