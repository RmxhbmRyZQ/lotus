package cn.flandre.lotus.http.session;

public interface Session {
    /**
     * 获取session中某个属性
     */
    public String getAttribute(String key);

    /**
     * 设置session的一个属性
     * @param key
     * @param value
     */
    public void setAttribute(String key, String value);

    /**
     * 更新session的属性值
     */
    public boolean updateAttribute();

    /**
     * 删除过期的session
     */
    public boolean refresh();

    /**
     * 获取本session的过期时间
     */
    public long getExpireTime();

    /**
     * 获取本session的ID
     */
    public String getID();
}
