package com.xuexibao.xserver.service.http;

import com.xuexibao.xserver.XServerConfig;

public class XHttpConfig {
    public static XHttpServer.ServerConfig load(XServerConfig xserverConfig) {
        XHttpServer.ServerConfig conf = new XHttpServer.ServerConfig();

        conf.port = xserverConfig.port;
        conf.host = xserverConfig.serverHost;

        return conf;
    }
}
