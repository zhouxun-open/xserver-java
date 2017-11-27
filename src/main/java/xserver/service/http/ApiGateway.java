package xserver.service.http;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import xserver.Config;
import xserver.exception.LogicalException;
import xserver.exception.UnInitilized;
import xserver.service.http.protocol.JsonProtocol;
import xserver.service.http.protocol.RetStatUtils;
import xserver.service.http.protocol.XProtocol;
import xserver.service.http.protocol.XReq;
import xserver.service.log.LogQueue;
import xserver.service.log.XLogger;
import xserver.util.ExceptionUtil;
import xserver.util.ServletUtil;

public class ApiGateway extends HttpServlet {
    private static final long serialVersionUID = 1356174584786644793L;
    public String msid;

    public static final String SERVER_HEADER = "xServer/3.0";

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ApiMethod {
        String url() default "";

        String apiName() default "";

        String[] middlewares() default {};

<<<<<<< HEAD
=======
        Class<?>[] methods() default {};

>>>>>>> 858ccacdb79cd2a989ea0107abb3f04858acd3e5
        Class<?> protocol() default JsonProtocol.class;

        int option() default JsonProtocol.DEFAULT_OPT;

        String strOption() default "";
    }

    public static class MethodInfo {
        public String moduleName;
        public List<Middleware> middlewares;
        public Method m;
        public String name;
        public Object apiSuite;
        public XProtocol protocol;
        public ParamInfo paramInfo;
    }

    private Map<String, MethodInfo> apiMap = new HashMap<String, MethodInfo>();

    public ApiGateway(String msid) {
        this.msid = msid;
    }

    public void registApiGateway(Object apiSuite) throws Exception {
        ApiClass api = apiSuite.getClass().getDeclaredAnnotation(ApiClass.class);
        if (null == api || null == api.value() || "".equals(api.value())) {
            throw new Exception();
        }
        XLogger logger = new XLogger(api.value());

        this._loadApiMap(apiSuite, api.value(), logger);
    }

    public void listLoadedApis() {
        for (Map.Entry<String, MethodInfo> e : this.apiMap.entrySet()) {
            System.err.println("\t" + e.getKey());
        }
    }

    private void _loadApiMap(Object apiSuite, String moduleName, XLogger logger) {
        Class<?> c = apiSuite.getClass();
        // 查找所有的api方法
        for (Method m : c.getDeclaredMethods()) {
            // Find 注解 ApiMethod
            ApiMethod apiMethodAnno = null;
            for (Annotation a : m.getDeclaredAnnotations()) {
                if (a.annotationType().equals(ApiMethod.class)) {
                    apiMethodAnno = (ApiMethod) a;
                    break;
                }
            }

            if (apiMethodAnno == null) {
                continue;
            }

            MethodInfo method = new MethodInfo();
            method.m = m;
            method.moduleName = moduleName;
            method.apiSuite = apiSuite;
            String[] middlewaresStr = apiMethodAnno.middlewares();
            List<Middleware> middlewares = new LinkedList<Middleware>();
            for (String middlewareStr : middlewaresStr) {
                Middleware middleware = MiddlewareManager.getInst().getMiddleware(middlewareStr);
                if (null != middleware) {
                    middlewares.add(middleware);
                }
            }
            method.middlewares = middlewares;

            method.name = apiMethodAnno.apiName();
            if (method.name.length() == 0) {
                method.name = m.getName();
            }

            String url = apiMethodAnno.url();
            if (null == url || 0 == url.length()) {
                url = String.format("/%s/api/%s", moduleName, method.name);
            }

            try {
                Class<?> protocolClass = apiMethodAnno.protocol();
                method.protocol = (XProtocol) protocolClass.newInstance();
                if (apiMethodAnno.option() != -1) {
                    method.protocol.setOption(apiMethodAnno.option());
                }
                method.protocol.setStrOption(apiMethodAnno.strOption());

                if (!method.protocol.isValidMethod(m)) {
                    String errInfo = String.format("Invalid method, %s %s", moduleName, method.name);
                    System.out.println(errInfo);
                    logger.err(errInfo);
                    continue;
                }
                ParamInfo paramInfo = new ParamInfo();
                Parameter[] params = m.getParameters();
                for (Parameter param : params) {
                    XParam xparam = new XParam();
                    xparam.clazz = param.getType();
                    if (false == param.getType().equals(XEnv.class) && false == param.getType().equals(XReq.class)) {
                        XReqParam anno = param.getAnnotation(XReqParam.class);
                        if (null == anno) {
                            String path = method.apiSuite.getClass().getPackage().getName() + ""
                                    + method.apiSuite.getClass().getName() + "." + method.m.getName();
                            throw new Exception(path + " param has no Annotation!");
                        } else {
                            xparam.required = anno.required();
                            xparam.value = anno.value();
                        }
                    }
                    paramInfo.addXParam(xparam);
                }
                method.paramInfo = paramInfo;
            } catch (Exception e) {
                logger.err(String.format("Create protocol instance exception, %s %s", moduleName, method.name), e);
                continue;
            }
            if (this.apiMap.containsKey(url)) {
                System.err.println("Path " + url + "is registed!");
                System.exit(-1000);
            }
            this.apiMap.put(url, method);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Server", ApiGateway.SERVER_HEADER);
        response.setHeader("ms_id", this.msid);
        String pathInfo = request.getPathInfo();// 接口访问路径

        XEnv env = new XEnv(request, response);
        env.pathInfo = pathInfo;

        MethodInfo method = null;
        try {
            method = this._service(env);
        } catch (Exception e) {
        }

        // Write access log
        try {
            this.logAccess(method, env);
        } catch (UnInitilized e) {
        }
    }

    protected MethodInfo _service(XEnv env) throws ServletException, IOException, UnInitilized {
        env.request.setCharacterEncoding("UTF-8");
        env.response.setCharacterEncoding("UTF-8");

        MethodInfo method = this.apiMap.get(env.pathInfo);
        boolean paramRoute = false;
        Map<String, Object> routeParam = new HashMap<String, Object>();
        if (null == method) {
            for (String pathInfo : this.apiMap.keySet()) {
                // route number param only
                if (env.pathInfo.matches(pathInfo.replaceAll(":[a-z|A-Z|0-9|_]*", "[0-9]*?[0-9]"))) {
                    String[] pathInfoArgs = env.pathInfo.split("/");
                    String[] registArgs = pathInfo.split("/");
                    if (registArgs.length <= pathInfoArgs.length) {
                        method = this.apiMap.get(pathInfo);
                        paramRoute = true;
                        for (int i = 0; i < registArgs.length; i++) {
                            if (registArgs[i].contains(":")) {
                                String key = registArgs[i].substring(1);
                                String value = pathInfoArgs[i];
                                routeParam.put(key, value);
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (method == null) {
            String text = RetStatUtils.getResponseText(Config.Error_Code.service_err, "找不到此接口");
            env.logInfo.responseTxt = text;
            ServletUtil.sendHttpResponseWithDefaultContentType(env.response, HttpServletResponse.SC_NOT_FOUND, text);
            env.logInfo.httpStatus = HttpServletResponse.SC_NOT_FOUND;
        } else {
            try {
                env.methodInfo = method;
                XReq req = method.protocol.parseRequest(env, method.apiSuite, method.m);
                if (paramRoute) {
                    req.params.putAll(routeParam);
                }
                method.protocol.excuteMiddleware(req);
                if (env.isContinue) {
                    method.protocol.process(req, method.apiSuite, method.m);
                }
                this.mergeLogParams(req);
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof LogicalException) {
                    LogicalException logical = (LogicalException) cause;
                    String text = RetStatUtils.getResponseText(logical.status, logical.msg, logical.reaultMap);
                    env.logInfo.responseTxt = text;
                    int code = logical.status == Config.Error_Code.no_auth ? HttpServletResponse.SC_UNAUTHORIZED
                            : HttpServletResponse.SC_OK;
                    env.response.addHeader("XCost",
                            Integer.toString((int) (System.currentTimeMillis() - env.reqStartTm)));
                    env.logInfo.httpStatus = code;
                    ServletUtil.sendHttpResponse(env.response, code, text);
                } else {
                    env.getLogger().err("API Processing Error", e);
                    String text = RetStatUtils.getResponseText(-1, "服务端异常");
                    env.logInfo.responseTxt = text;
                    env.logInfo.httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    env.response.addHeader("XCost",
                            Integer.toString((int) (System.currentTimeMillis() - env.reqStartTm)));
                    ServletUtil.sendHttpResponseWithDefaultContentType(env.response,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
                    env.logInfo.logErrors = ExceptionUtil.getErrorInfoFromException(e);
                }
            }
        }
        env.logInfo.costTime = (int) (System.currentTimeMillis() - env.reqStartTm);

        return method;
    }

    private void mergeLogParams(XReq req) {
        req.params.putAll(req.env.params);
        req.params.putAll(req.env.logParams);
        req.env.logParams = req.params;
    }

    protected void logAccess(MethodInfo method, XEnv env) throws UnInitilized {
        env.logInfo.params = JSON.toJSONString(env.params);
        env.logInfo.logParams = JSON.toJSONString(env.logParams);
        LogQueue.push(env.logInfo);
        final char SEP = '\t';
        StringBuffer sb = new StringBuffer(256);
        sb.append(env.ip);
        sb.append(SEP);
        sb.append(env.reqId);
        sb.append(SEP);
        sb.append(System.currentTimeMillis() - env.reqStartTm);
        sb.append(SEP);
        sb.append(env.response.getStatus());
        sb.append(SEP);
        sb.append(env.pathInfo);
        sb.append(SEP);
        sb.append(env.status);
        sb.append(SEP);
        sb.append("cookies:{");
        if (null != env.cookies) {
            for (Cookie cookie : env.cookies) {
                sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
            }
        }
        sb.append("}");
        sb.append(SEP);
        for (Map.Entry<String, Long> e : env.timings.entrySet()) {
            sb.append(e.getKey());
            sb.append('=');
            sb.append(e.getValue());
            sb.append(',');
        }
        sb.append(SEP);
        sb.append("=====logParams=");
        String logParams = JSON.toJSONString(env.logParams);
        sb.append(logParams);

        sb.append(SEP);
        if (env.msg != null) {
            sb.append(env.msg);
        }

        env.getLogger().acc(sb.toString());
    }
}
