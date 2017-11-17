package xserver.service.db.xmysql;

public class XMySqlConfig {
    public String url;
    public String user;
    public String password;
    public int maxActive = 100;
    public int maxIdle = 60;
    public int minIdle = 10;
    public long maxWait = 200;
    public int numTestsPerEvictionRun = 20;
    public long timeBetweenEvictionRunsMillis = 5000;
    public long minEvictableIdleTimeMillis = 10000;
}
