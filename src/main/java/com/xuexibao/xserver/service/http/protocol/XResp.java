package com.xuexibao.xserver.service.http.protocol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class XResp {
    public int httpCode;
    public int status;
    public String msg;
    public String result;
    public Boolean success;
    public Map<String, Object> resultMap;
    public List<Cookie> cookies;
    public Map<String, String> headers;

    public XResp() {
        this(0, "OK");
    }

    public XResp(int status) {
        this(status, "");
    }

    public XResp(String msg) {
        this(0, msg);
    }

    public XResp(int status, String msg) {
        this.status = status;
        if (status >= 0) {
            this.success = true;
        }
        this.msg = msg;
        this.httpCode = HttpServletResponse.SC_OK;
        this.resultMap = new HashMap<String, Object>();
        this.headers = new HashMap<String, String>();
        this.cookies = new LinkedList<Cookie>();
    }

    public void addCookie(Cookie cookie) {
        if (null == cookie) {
            return;
        }

        this.cookies.add(cookie);
    }

    public void setHeader(String headerName, String headerValue) {
        if (null == headerName || 0 == headerName.length() || null == headerValue || 0 == headerValue.length()) {
            return;
        }

        this.headers.put(headerName, headerValue);
    }
}