package xserver.service.http.protocol;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import xserver.exception.LogicalException;
import xserver.service.http.XEnv;

public class RetStatUtils {
    public static String getResponseText(int status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);

        return RetStatUtils.getResponseText(map);
    }

    public static String getResponseText(int status, String msg) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        if (null != msg && 0 != msg.length()) {
            map.put("msg", msg);
        }

        return RetStatUtils.getResponseText(map);
    }

    public static String getResponseText(int status, String msg, Map<String, Object> resultMap) {
        if (null == resultMap) {
            resultMap = new HashMap<String, Object>();
        }
        resultMap.put("status", status);
        if (null != msg && 0 != msg.length()) {
            resultMap.put("msg", msg);
        }

        return RetStatUtils.getResponseText(resultMap);
    }

    public static String getResponseText(Map<String, Object> map) {
        String text = JSON.toJSONString(map);

        return text;
    }

    public static String getResponseTextByLGException(XEnv env, LogicalException ex) {
        if (ex.reaultMap == null) {
            ex.reaultMap = new HashMap<String, Object>();
            ex.reaultMap.put("reqId", env.reqId);
        }
        return RetStatUtils.getResponseText(ex.status, ex.msg, ex.reaultMap);
    }
}
