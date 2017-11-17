package xserver.service.http;

import xserver.XServerConfig;

public class XHttpConfig {
    public static XHttpServer.ServerConfig load(XServerConfig xserverConfig) {
        XHttpServer.ServerConfig conf = new XHttpServer.ServerConfig();

        conf.port = xserverConfig.port;
        conf.host = xserverConfig.serverHost;

        return conf;
    }
}
