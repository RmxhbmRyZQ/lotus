package cn.flandre.lotus.http.session;

public interface Session {
    public String getAttribute(String key);

    public void setAttribute(String key, String value);

    public boolean updateAttribute();

    public boolean refresh();

    public long getExpireTime();

    public String getID();
}
