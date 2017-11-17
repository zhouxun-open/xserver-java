package xserver.service.http.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import xserver.Config;
import xserver.exception.LogicalException;
import xserver.exception.ParamException;
import xserver.service.http.Middleware;
import xserver.service.http.ParamInfo;
import xserver.service.http.XEnv;
import xserver.service.http.XParam;
import xserver.util.ExceptionUtil;
import xserver.util.ServletUtil;

public class JsonProtocol implements XProtocol {

    public static final int POST = 0x00000001;
    public static final int GET = 0x00000002;
    public static final int PARSE_COOKIE = 0x00000004;
    public static final int PARSE_USERAGENT = 0x00000008;

    public static final int DEFAULT_OPT = JsonProtocol.GET | JsonProtocol.POST | JsonProtocol.PARSE_COOKIE
            | JsonProtocol.PARSE_USERAGENT;
    public static final int POST_OPT = JsonProtocol.POST | JsonProtocol.PARSE_COOKIE | JsonProtocol.PARSE_USERAGENT;
    public static final int GET_OPT = JsonProtocol.GET | JsonProtocol.PARSE_COOKIE | JsonProtocol.PARSE_USERAGENT;

    protected int option;
    protected long maxRequestBodyLength;

    public JsonProtocol() {
        this.option = JsonProtocol.DEFAULT_OPT;
        this.maxRequestBodyLength = 1024 * 1024 * 10; // 10MB
    }

    @Override
    public XReq parseRequest(XEnv env, Object apiSuite, Method m) throws Exception {
        XReq req = new XReq(env);

        this.parseQueryString(env, req);
        this.parseCookie(env, req);
        this.parseBody(env, req);

        return req;
    }

    @Override
    public void process(XReq req, Object apiSuite, Method m) throws Exception {
        req.env.logInfo.params = JSON.toJSONString(req.params);
        XResp resp = this.invoke(req.env, req, apiSuite, m);

        if (resp == null) {
            return;
        }

        this.sendJsonResponse(req.env, resp);
    }

    protected void parseQueryString(XEnv env, XReq req) {
        if ((this.option & JsonProtocol.GET) != 0) {
            Map<String, String> queryStringParams = ServletUtil.decodeQueryString(env.request.getQueryString());
            if (queryStringParams != null) {
                env.logInfo.queryString = env.request.getQueryString();
                req.params.putAll(queryStringParams);
            }
        }
    }

    protected void parseCookie(XEnv env, XReq req) {
        if ((this.option & JsonProtocol.PARSE_COOKIE) != 0) {
            Cookie[] cookies = env.request.getCookies();
            req.cookies = cookies;
            this.logCookie(env, cookies);
        }
    }

    private void logCookie(XEnv env, Cookie[] cookies) {
        StringBuffer sb = new StringBuffer(256);
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
            }
        }
        String cookieStr = sb.toString();
        env.logInfo.reqCookies = cookieStr;
    }

    protected boolean parseBody(XEnv env, XReq req) throws Exception {
        if ((this.option & JsonProtocol.POST) != 0) {
            Map<String, Object> params = null;
            // Read HTTP request body
            byte[] body;
            try {
                body = ServletUtil.readPostData(env.request, this.maxRequestBodyLength);
                env.body = body;
            } catch (Exception e) {
                env.getLogger().err("Read post data exception", e);
                String text = RetStatUtils.getResponseText(Config.Error_Code.no_content, "post流为空");
                ServletUtil.sendHttpResponseWithDefaultContentType(env.response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
                return false;
            }
            params = this.parseBody(body, env);

            if (params == null) {
                String text = RetStatUtils.getResponseText(Config.Error_Code.args_err, "参数错误");
                ServletUtil.sendHttpResponseWithDefaultContentType(env.response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
                return false;
            }

            req.params.putAll(params);
        }
        return true;
    }

    protected XResp invoke(XEnv env, XReq req, Object apiSuite, Method m) throws Exception {
        XResp resp;
        try {
            // Invoke API
            List<Object> params = new ArrayList<Object>();
            ParamInfo paramInfo = env.methodInfo.paramInfo;
            if (paramInfo != null && paramInfo.notEmpty()) {
                for (int i = 0; i < paramInfo.xparams.size(); i++) {
                    XParam xparam = paramInfo.xparams.get(i);
                    if (xparam.clazz == XReq.class) {
                        params.add(req);
                    } else if (xparam.clazz == XEnv.class) {
                        params.add(env);
                    } else {
                        Object value = req.paramGetObject(xparam.value, xparam.required, xparam.clazz, true);
                        params.add(value);
                    }
                }
            }
            resp = (XResp) m.invoke(apiSuite, params.toArray());
            return resp;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            env.logInfo.logErrors = ExceptionUtil.getErrorInfoFromException(e);
            if (cause instanceof LogicalException || cause instanceof ParamException) {
                LogicalException _cause = (LogicalException) cause;
                env.getLogger().err(_cause.status + "\t" + _cause.msg);
                String text = RetStatUtils.getResponseTextByLGException(env, _cause);
                env.logInfo.responseTxt = text;
                ServletUtil.sendHttpResponseWithDefaultContentType(env.response, _cause.httpCode, text);
                env.logInfo.httpStatus = _cause.httpCode;
                return null;
            } else {
                env.getLogger().err("Process API exception", cause);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("reqId", env.reqId);
                String text = RetStatUtils.getResponseText(Config.Error_Code.service_err, "unknown exception", map);
                env.logInfo.responseTxt = text;
                ServletUtil.sendHttpResponseWithDefaultContentType(env.response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
                env.logInfo.httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                return null;
            }
        } catch (Exception e) {
            env.logInfo.logErrors = ExceptionUtil.getErrorInfoFromException(e);
            if (e instanceof LogicalException || e instanceof ParamException) {
                LogicalException _cause = (LogicalException) e;
                env.getLogger().err(_cause.status + "\t" + _cause.msg);
                String text = RetStatUtils.getResponseTextByLGException(env, _cause);
                env.logInfo.responseTxt = text;
                ServletUtil.sendHttpResponseWithDefaultContentType(env.response, _cause.httpCode, text);
                env.logInfo.httpStatus = _cause.httpCode;
                return null;
            }
            env.getLogger().err("Process API exception", e);
            env.logInfo.logErrors = ExceptionUtil.getErrorInfoFromException(e);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("reqId", env.reqId);
            String text = RetStatUtils.getResponseText(Config.Error_Code.service_err, "unknown exception", map);
            env.logInfo.responseTxt = text;
            ServletUtil.sendHttpResponseWithDefaultContentType(env.response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text);
            env.logInfo.httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            return null;
        }
    }

    protected Map<String, Object> parseBody(byte[] body, XEnv env) throws Exception {
        if (body.length == 0) {
            return new HashMap<String, Object>();
        }

        // 默认是JSON，iOS传的是JSON，安卓传的是K1=V1&K2=V2，所以需要在这儿做兼容
        try {
            String bodyText = new String(body, "UTF-8");
            env.logInfo.body = bodyText;
            env.logParam("======body", bodyText);
            Map<String, Object> params = JSON.parseObject(bodyText);
            return params;
        } catch (Exception e) {
            // 如果parse失败说明是K=V对
            try {
                String bodyText = new String(body, "UTF-8");
                String[] args = bodyText.split("&");
                Map<String, Object> params = new HashMap<String, Object>();
                for (String arg : args) {
                    String[] pair = arg.split("=");
                    if (pair.length >= 2) {
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = URLDecoder.decode(pair[1], "UTF-8");
                        params.put(key, value);
                    }
                }
                return params;
            } catch (Exception throwable) {
                env.getLogger().err("解析post流失败", throwable);
            }
        }

        return new HashMap<String, Object>();
    }

    public void sendJsonResponse(XEnv env, XResp resp) throws Exception {
        Map<String, String> headers = resp.headers;
        Map<String, Object> resultMap = resp.resultMap;
        String text = resp.result;
        if (resp.status == Config.Error_Code.no_auth) {
            resp.httpCode = HttpServletResponse.SC_FORBIDDEN;
        }
        if (null == text || 0 == text.length()) {
            resultMap.put("status", resp.status);
            if (null != resp.msg && 0 != resp.msg.length()) {
                resultMap.put("msg", resp.msg);
            } else {
                resultMap.put("msg", "OK");
            }
            resultMap.put("success", resp.success);
            text = JSON.toJSONString(resultMap);
        }
        env.logParam("responseText", text);
        env.logInfo.responseTxt = text;
        env.logInfo.httpStatus = resp.httpCode;
        env.logInfo.logParams = JSON.toJSONString(env.logParams);
        List<Cookie> cookies = resp.cookies;
        if (null != env.cookies) {
            for (Cookie cookie : env.cookies) {
                cookies.add(cookie);
            }
        }
        this.sendResponse(env, resp.httpCode, headers, cookies, text);
    }

    public void sendResponse(XEnv env, int httpCode, Map<String, String> headers, List<Cookie> cookies, String text)
            throws Exception {
        boolean setContentType = false;

        if (env.response.isCommitted()) {
            env.getLogger().err("Response already been output some where, check your code please.");
            return;
        }

        env.response.addHeader("XCost", Integer.toString((int) (System.currentTimeMillis() - env.reqStartTm)));

        if (null != headers && 0 != headers.size()) {
            for (String header : headers.keySet()) {
                env.response.addHeader(header, headers.get(header));
                if (header.equals("Content-Type")) {
                    setContentType = true;
                }
            }
        }

        if (null != cookies && 0 != cookies.size()) {
            for (Cookie cookie : cookies) {
                env.response.addCookie(cookie);
            }
        }

        if (false == setContentType) {
            ServletUtil.sendHttpResponseWithDefaultContentType(env.response, httpCode, text);
        } else {
            ServletUtil.sendHttpResponse(env.response, httpCode, text);
        }
    }

    @Override
    public void setOption(int option) {
        this.option = option;
    }

    @Override
    public void setStrOption(String strOption) {
    }

    @Override
    public boolean isValidMethod(Method m) {
        if (m.getReturnType() == XResp.class) {
            return true;
        }
        return false;
    }

    @Override
    public void excuteMiddleware(XReq req) throws Exception {
        List<Middleware> middlewares = req.env.methodInfo.middlewares;
        if (null == middlewares || 0 == middlewares.size()) {
            return;
        }
        for (Middleware middleware : middlewares) {
            middleware.method.invoke(middleware.middleObj, req);
        }
    }
}
