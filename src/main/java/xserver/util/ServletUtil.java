package xserver.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import xserver.Config;
import xserver.exception.LogicalException;
import xserver.exception.ReadPostException;

public class ServletUtil {
<<<<<<< HEAD
=======

>>>>>>> 858ccacdb79cd2a989ea0107abb3f04858acd3e5
    /**
     * 解析url中的参数
     *
     * @param queryString
     * @return
     */
    public static Map<String, String> decodeQueryString(String queryString) {
        if (queryString == null || queryString.length() == 0) {
            return null;
        }

        MultiMap<String> map = new MultiMap<String>();
        byte[] queryStringBytes = queryString.getBytes();
        UrlEncoded.decodeUtf8To(queryStringBytes, 0, queryStringBytes.length, map);

        if (map.size() == 0) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        Enumeration<String> em = Collections.enumeration(map.keySet());
        while (em.hasMoreElements()) {
            String name = em.nextElement();
            result.put(name, map.getValue(name, 0));
        }
        return result;
    }

    /**
     * 判断HTTP请求的method是否合法
     *
     * @param request
     *            请求对象
     * @param methods
     *            接受的方法类型
     */
    public static void validateRequestWithMethod(HttpServletRequest request, String[] methods) throws Exception {
        String method = request.getMethod().toUpperCase();
        for (String m : methods) {
            if (m.trim().equalsIgnoreCase(method)) {
                return;
            }
        }
        throw new LogicalException(Config.Error_Code.method_err, Config.Error_Code_Text.method_err);
    }

    public static void validateRequestWithDefaultMethod(HttpServletRequest request) throws Exception {
        String[] methods = new String[] { "GET", "POST" };
        ServletUtil.validateRequestWithMethod(request, methods);
    }

    /**
     * 获取post body里的数据
     *
     * @param request
     * @param max
     * @return
     * @throws IOException
     * @throws ReadPostException
     */
    public static byte[] readPostData(HttpServletRequest request, long max) throws IOException, ReadPostException {
        int bodyLen = request.getContentLength();
        if (bodyLen > max) {
            throw new ReadPostException(String.format("Entity Too Large, max:%d cur:%d", max, bodyLen));
        }
        if (bodyLen == -1) {
            bodyLen = 0;
        }

        byte[] body = new byte[bodyLen];
        ServletInputStream is = request.getInputStream();
        int pos = 0;

        while (pos < bodyLen) {
            int received = is.read(body, pos, bodyLen - pos);
            if (received == -1) {
                break;
            }
            pos += received;
        }

        if (pos != bodyLen) {
            throw new ReadPostException(
                    String.format("Client Sent Less Data Than Expected, expected:%s cur:%s", bodyLen, pos));
        }

        return body;
    }

    /**
     * 设置请求返回头信息
     *
     * @param response
     * @param httpCode
     * @param respText
     * @throws IOException
     */
    public static void sendHttpResponseWithDefaultContentType(HttpServletResponse response, Integer httpCode,
            String respText) throws IOException {
        httpCode = (httpCode == null) ? 200 : httpCode;
        response.setStatus(httpCode);
        response.setContentType("text/plain;charset=UTF-8");
        byte[] respBin = respText.getBytes();
        response.setContentLength(respBin.length);

        ServletOutputStream os = response.getOutputStream();
        os.write(respBin);
        os.flush();
        os.close();
    }

    public static void sendHttpResponse(HttpServletResponse response, Integer httpCode, String respText)
            throws IOException {
        httpCode = (httpCode == null) ? 200 : httpCode;
        response.setStatus(httpCode);
        response.setContentType("text/plain;charset=UTF-8");
        byte[] respBin = respText.getBytes();
        response.setContentLength(respBin.length);

        ServletOutputStream os = response.getOutputStream();
        os.write(respBin);
        os.flush();
        os.close();
    }

<<<<<<< HEAD
=======
    static final String digits = "0123456789ABCDEF";

>>>>>>> 858ccacdb79cd2a989ea0107abb3f04858acd3e5
    public static void sendCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    public static void sendCookies(HttpServletResponse response, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            response.addCookie(cookie);
        }
    }
}
