package com.xuexibao.xserver.service.http;

//@javadoc

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xuexibao.xserver.exception.UnInitilized;
import com.xuexibao.xserver.service.http.ApiGateway.MethodInfo;
import com.xuexibao.xserver.service.log.ApiLogInfo;
import com.xuexibao.xserver.service.log.XLogger;
import com.xuexibao.xserver.util.Base64;

public class XEnv {
    /**
     * Serrvlet HTTP 请求对象
     */
    public HttpServletRequest request;

    /**
     * Servlet HTTP 返回对象
     */
    public HttpServletResponse response;

    public Long loginUserId;

    /**
     * Cookie
     */
    public Cookie[] cookies;

    /**
     * 客户端 IP
     */
    public String ip;

    /**
     * 客户端 MAC地址
     */
    public String mac;

    /**
     * 全局唯一的请求 ID, 将会被写入访问日志和错误日志
     */
    public String reqId;

    /**
     * 用于存放插件自定义数据的 Map 对象
     */
    public Map<String, Object> sticker;

    /**
     * post传递的数据
     */
    public Map<String, Object> params;

    public Map<String, Object> middlewareParam;

    /**
     * body
     */
    public byte[] body;
    public MethodInfo methodInfo;

    public long reqStartTm;
    public long timingBeginTm;
    public Map<String, Long> timings;
    public Map<String, Object> logParams; // 输出日志，也写Audit
    public Map<String, Object> extResultMap;
    public String msg;
    public int status;
    public String pathInfo;
    public boolean isContinue;
    public ApiLogInfo logInfo = new ApiLogInfo();
    private XLogger logger;
    public ParamInfo paramInfo;

    public XEnv() {
        this.reqStartTm = System.currentTimeMillis();
        this.reqId = XEnv.makeRequestId();
        this.isContinue = true;
        this.timingBeginTm = this.reqStartTm;
        this.timings = new HashMap<String, Long>();
        this.logParams = new HashMap<String, Object>();
        this.extResultMap = new HashMap<String, Object>();
        this.sticker = new HashMap<String, Object>();
    }

    public XEnv(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.ip = this.getRealIp(request);

        this.reqStartTm = System.currentTimeMillis();
        this.reqId = XEnv.makeRequestId();
        this.timingBeginTm = this.reqStartTm;
        this.isContinue = true;
        this.timings = new HashMap<String, Long>();
        this.logParams = new HashMap<String, Object>();
        this.sticker = new HashMap<String, Object>();
        this.extResultMap = new HashMap<String, Object>();
        response.addHeader("XRequestId", this.reqId);
        this.pathInfo = request.getPathInfo();
        this.logInfo.requestId = this.reqId;
        this.logInfo.url = this.pathInfo;
        this.logInfo.IP = this.ip;
        this.logInfo.time = this.reqStartTm;
    }

    private String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public void startTimming() {
        this.timingBeginTm = System.currentTimeMillis();
    }

    public void logTiming(String operation) {
        long cost = System.currentTimeMillis() - this.timingBeginTm;
        this.timings.put(operation, cost);
    }

    public void logParam(String name, Object value) {
        this.logParams.put(name, value);
    }

    public XLogger getLogger() throws UnInitilized {
        if (this.logger == null) {
            this.logger = XLogger.getInst(String.format("ReqId:%s", this.reqId));
        }
        return this.logger;
    }

    public static String makeRequestId() {
        long uuid = UUID.randomUUID().getMostSignificantBits();
        byte[] uuidBytes = ByteBuffer.allocate(8).putLong(uuid).array();
        return Base64.encode(uuidBytes);
    }
}