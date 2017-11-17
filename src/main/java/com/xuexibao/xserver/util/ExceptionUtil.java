package com.xuexibao.xserver.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    /**
     * 将异常信息e.printStackTrace()转换成string字符串
     *
     * @param e
     * @return
     */
    public static String getErrorInfoFromException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String errorInfoS = sw.toString();
        if (errorInfoS != null && errorInfoS.length() > 3090) {
            errorInfoS = errorInfoS.substring(0, 3090);
        }
        return errorInfoS;
    }

}
