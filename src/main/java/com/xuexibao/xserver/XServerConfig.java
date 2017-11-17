package com.xuexibao.xserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xuexibao.xserver.service.db.xmysql.XMySqlConfig;
import com.xuexibao.xserver.service.db.xredis.XRedisConfig;

public class XServerConfig {

    private static XServerConfig xserverConfig;

    public String serverHost;
    public String env;
    public int port;
    public XServerLogConfig log;
    public Map<String, XMySqlConfig> mysqlConfig;
    public Map<String, XRedisConfig> redisConfig;

    public static XServerConfig getInst() throws Exception {
        if (null == XServerConfig.xserverConfig) {
            throw new Exception("xserver config is not init");
        }
        return XServerConfig.xserverConfig;
    }

    public static XServerConfig createConfig(String path) throws Exception {
        String str = XServerConfig.readFile(path).replaceAll("\\s", "");
        XServerConfig.xserverConfig = JSON.parseObject(str, XServerConfig.class);
        return XServerConfig.xserverConfig;
    }

    public static String readFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (file.exists() && file.canRead()) {
            InputStream input = new FileInputStream(file);
            byte[] b = new byte[input.available()];
            input.read(b);
            input.close();
            return new String(b, "UTF-8");
        } else {
            throw new Exception("file is not exist or not read");
        }
    }

    public XMySqlConfig getMySqlConfig(String dbname) {
        return this.mysqlConfig.get(dbname);
    }

    public XRedisConfig getRedisConfig(String dbname) {
        return this.redisConfig.get(dbname);
    }
}
