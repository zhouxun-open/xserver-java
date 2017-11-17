package com.xuexibao.xserver;

import java.lang.management.ManagementFactory;

import com.xuexibao.xserver.service.http.ApiGateway;
import com.xuexibao.xserver.service.http.MiddlewareManager;
import com.xuexibao.xserver.service.http.XHttpConfig;
import com.xuexibao.xserver.service.http.XHttpServer;
import com.xuexibao.xserver.service.http.XHttpServer.ServerConfig;
import com.xuexibao.xserver.service.log.LogQueue;
import com.xuexibao.xserver.service.log.XLogger;

public class XServer {
    private ApiGateway apiGateway;
    public XServerConfig xserverConfig;

    public XServer(String[] args) throws Exception {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        System.err.println("xServer pid: " + pid);

        String configPath = null;
        if (null == args || 0 == args.length) {
            System.err.println("Please specify configuration file path on starting up.");
            System.exit(-1);
        } else {
            configPath = args[0];
        }
        XServerConfig config = XServerConfig.createConfig(configPath);
        this.init(config);
    }

    private void init(XServerConfig config) throws Exception {
        this.xserverConfig = config;
        this.apiGateway = new ApiGateway(String.format("%d", config.port));

        // 初始化xserver的服务
        this.initService(config);

        try {
            LogQueue queue = LogQueue.getInst();
            queue.start();
        } catch (Exception e) {
            System.err.println("Open log access exception.");
            e.printStackTrace();
            System.exit(-4);
        }
    }

    public void start() throws Exception {
        System.err.println("Registed api list:");
        this.apiGateway.listLoadedApis();
        System.err.println("Server listen on port :" + XServerConfig.getInst().port);
        XHttpServer.getInst().registApiService("/", this.apiGateway);
        XHttpServer.startService();
    }

    public void registApi(Object apiSuite) throws Exception {
        this.apiGateway.registApiGateway(apiSuite);
    }

    private void initService(XServerConfig xserverConfig) throws Exception {
        ServerConfig config = XHttpConfig.load(xserverConfig);
        int port = config.port;
        if (port > 0 && port <= 65535) {
            config.port = port;
        } else {
            throw new Exception("port " + port + " is not valid");
        }

        try {
            // 初始化日志服务
            XLogger.init(xserverConfig.log);
        } catch (Exception e) {
            System.err.println("Initialize logger exception.");
            e.printStackTrace();
            System.exit(-3);
        }

        try {
            // 初始化http服务
            XHttpServer.init(config);
        } catch (Exception e) {
            System.err.println("Initialize HttpServer exception.");
            e.printStackTrace();
            System.exit(-4);
        }
    }

    public void registMiddleware(Object middlewareObj) {
        MiddlewareManager.getInst().regist(middlewareObj);
    }
}
