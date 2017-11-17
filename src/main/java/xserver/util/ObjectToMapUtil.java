package xserver.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectToMapUtil {
    public static Map<String, Object> getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        Map<String, Object> infoMap = new HashMap<String, Object>();
        for (int i = 0; i < fields.length; i++) {
            Object fileValue = ObjectToMapUtil.getFieldValueByName(fields[i].getName(), o);
            if (null != fileValue) {
                infoMap.put(fields[i].getName(), fileValue);
            }
        }
        return infoMap;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T getObjectByMap(T t, Map<String, Object> map)
            throws InstantiationException, IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                field.set(t, map.get(field.getName()));
            } catch (Exception e) {
            }
        }
        return t;
    }
}
