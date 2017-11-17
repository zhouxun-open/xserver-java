package com.xuexibao.xserver.util;

public class StringUtil {
    public static String underLineToCamel(String str) {
        String split[] = str.split("_");
        String camel = "";
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                camel += split[i].substring(0, 1).toLowerCase() + split[i].substring(1).toString();
            } else {
                camel += split[i].substring(0, 1).toUpperCase() + split[i].substring(1);
            }
        }
        return camel;
    }

    public static String camelToUnderLine(String camel) {
        String str = "";
        for (int i = 0; i < camel.length(); i++) {
            char temp = camel.charAt(i);
            if (temp >= 'A' && temp <= 'Z') {
                str += "_" + Character.toLowerCase(temp);
            } else {
                str += temp;
            }
        }
        if (str.startsWith("_")) {
            str = str.substring(1);
        }
        return str;
    }

    /**
     * 检测是否为空字符串
     *
     * @param str
     * @return
     */
    public static boolean checkNullAndEmpty(String str) {
        if (null == str || str.length() == 0) {
            return true;
        }
        return false;
    }
}