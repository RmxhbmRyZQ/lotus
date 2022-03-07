package cn.flandre.lotus.http.session;

public class MemorySession implements Session {
    @Override
    public String getAttribute(String key) {
        return null;
    }

    @Override
    public void setAttribute(String key, String value) {

    }

    @Override
    public boolean updateAttribute() {
        return false;
    }

    @Override
    public boolean refresh() {
        return false;
    }

    @Override
    public long getExpireTime() {
        return 0;
    }

    @Override
    public String getID() {
        return null;
    }
}
