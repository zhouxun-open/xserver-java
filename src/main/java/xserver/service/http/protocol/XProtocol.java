package xserver.service.http.protocol;

import java.lang.reflect.Method;

import xserver.service.http.XEnv;

public interface XProtocol {
    public void process(XReq req, Object apiSuite, Method m) throws Exception;

    public XReq parseRequest(XEnv env, Object apiSuite, Method m) throws Exception;

    public void setOption(int option);

    public void setStrOption(String setStrOption);

    public boolean isValidMethod(Method m);

    public void excuteMiddleware(XReq req) throws Exception;
}