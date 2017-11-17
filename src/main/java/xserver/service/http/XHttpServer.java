/**
 * @author Xun_Zhou
 */

package xserver.service.http;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import xserver.exception.LogicalException;
import xserver.exception.UnInitilized;

public class XHttpServer {

    public static class ServerConfig {
        public String host;
        public int port;

        public boolean customizeThreadPool = false;
        public int maxThread = Integer.MAX_VALUE;
        public int minThread = 100;
        public int threadIdleTimeout = 100 * 1000;

        public int ioTimeout = 30000;
    }

    private static Server jettyServer;
    private static List<Handler> handlers;
    private static XHttpServer httpServer;

    private XHttpServer() {
    };

    /**
     * 注册 Servlet 服务 路径为urlPrefix
     *
     * @throws LogicalException
     *             如果注册的urlPrefix为空，抛出异常
     */
    public void registApiService(String urlPrefix, Servlet servlet) throws LogicalException {
        if (null == urlPrefix) {
            throw new LogicalException(-100, "错误的url前缀");
        }
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(urlPrefix);
        context.addServlet(new ServletHolder(servlet), "/*");
        XHttpServer.handlers.add(context);
    }

    public static XHttpServer getInst() throws UnInitilized {
        if (XHttpServer.httpServer == null) {
            XHttpServer.httpServer = new XHttpServer();
        }

        return XHttpServer.httpServer;
    }

    public static void init(ServerConfig conf) throws Exception {
        if (XHttpServer.jettyServer != null) {
            throw new Exception("HttpServer already initialized.");
        }

        QueuedThreadPool pool = new QueuedThreadPool();
        if (conf.customizeThreadPool) {
            pool.setMaxThreads(conf.maxThread);
            pool.setMinThreads(conf.minThread);
            pool.setIdleTimeout(conf.threadIdleTimeout);
        }

        XHttpServer.jettyServer = new Server(pool);

        // Set the http connector
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        ServerConnector http = new ServerConnector(XHttpServer.jettyServer, new HttpConnectionFactory(httpConfig));
        http.setHost(conf.host);
        http.setPort(conf.port);
        http.setIdleTimeout(conf.ioTimeout);
        XHttpServer.jettyServer.addConnector(http);

        XHttpServer.handlers = new LinkedList<Handler>();

        XHttpServer.removeServerHeader(XHttpServer.jettyServer);
    }

    public static void removeServerHeader(Server server) {
        for (Connector y : server.getConnectors()) {
            for (ConnectionFactory x : y.getConnectionFactories()) {
                if (x instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory) x).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
    }

    public static void startService() throws Exception {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(XHttpServer.handlers.toArray(new Handler[0]));
        XHttpServer.jettyServer.setHandler(contexts);

        XHttpServer.jettyServer.start();
        XHttpServer.jettyServer.join();
    }
}