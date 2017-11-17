package xserver.service.http.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.alibaba.fastjson.JSON;

import xserver.exception.ParamException;
import xserver.service.http.XEnv;

public class XReq {
    public XEnv env;
    public Map<String, Object> params = new HashMap<String, Object>();
    public Cookie[] cookies = {};

    public XReq(XEnv env) {
        this.env = env;
        this.env.params = this.params;
        this.env.cookies = this.cookies;
    }

    /**
     * 获取客户端传递过来的Long类型参数 默认写到日志里
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv)
            throws ParamException {
        return this.paramGetNumber(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取客户端传递过来的Long类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写到日志里
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv, boolean logParam)
            throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Long) || (o instanceof Integer) || (o instanceof Short)) {
            return Long.parseLong(String.valueOf(o));
        }

        try {
            if (throwExceptionWhenValueIsEmpty) {
                if (strConv) {
                    String str = o.toString();
                    return Long.parseLong(str);
                }
                throw new ParamException("");
            } else {
                if (null != o) {
                    String str = o.toString();
                    if (0 != str.length()) {
                        return Long.parseLong(str);
                    }
                }
                return null;
            }
        } catch (Exception e) {
            throw new ParamException("Param '" + name + "' should be number.");
        }
    }

    /**
     * 获取客户端传递过来的整型参数 默认写到日志里
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws ParamException
     */
    public Integer paramGetInteger(String name, boolean throwExceptionWhenValueIsEmpty) throws ParamException {
        return this.paramGetInteger(name, throwExceptionWhenValueIsEmpty, true, Integer.class);
    }

    /**
     * 获取客户端传递过来的整型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写到日志里
     * @param type
     *            指定参数类型
     * @return
     * @throws ParamException
     */
    public Integer paramGetInteger(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam, Class<?> type)
            throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Integer) || (o instanceof Short)) {
            return Integer.parseInt(o.toString());
        } else if (o instanceof String) {
            try {
                return Integer.parseInt(o.toString());
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be number.");
            }
        }

        throw new ParamException("Param '" + name + "' should be integer.");
    }

    /**
     * 获取客户端传递过来的Long类型参数
     *
     * @param name
     *            参数名
     * @param min
     *            允许的最大值
     * @param max
     *            允许的最小值
     * @param logParam
     *            是否写到日志里
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, long min, long max, boolean strConv, boolean logParam)
            throws ParamException {
        Long number = this.paramGetNumber(name, true, strConv, logParam);
        if (number < min || number > max) {
            throw new ParamException("Param '" + name + "' out of range");
        }
        return number;
    }

    /**
     * 获取double型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param strConv
     *            是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Double paramGetDouble(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv)
            throws ParamException {
        return this.paramGetDouble(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取double型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param strConv
     *            是否从字符串中转换
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public Double paramGetDouble(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv, boolean logParam)
            throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof Double) || (o instanceof Float)) {
            return (Double) o;
        }

        if (strConv && (o instanceof String)) {
            try {
                return Double.parseDouble((String) o);
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be double.");
            }
        }

        throw new ParamException("Param '" + name + "' should be double.");
    }

    /**
     * 获取字符串型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否必填参数(为空则抛出异常)
     * @return
     * @throws ParamException
     */
    public String paramGetString(String name, boolean throwExceptionWhenValueIsEmpty) throws ParamException {
        return this.paramGetString(name, throwExceptionWhenValueIsEmpty, true);
    }

    /**
     * 获取字符串型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public String paramGetString(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam)
            throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        if ((o instanceof String)) {
            String s = (String) o;
            if (throwExceptionWhenValueIsEmpty && s.length() == 0) {
                throw new ParamException("Param '" + name + "' should not be empty.");
            }
            return s;
        }

        throw new ParamException("Param '" + name + "' should be string.");
    }

    /**
     * 获取布尔类型参数 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串转换
     * @return
     * @throws ParamException
     */
    public Boolean paramGetBoolean(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv)
            throws ParamException {
        return this.paramGetBoolean(name, throwExceptionWhenValueIsEmpty, strConv, true);
    }

    /**
     * 获取布尔类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param strConv
     *            是否从字符串转换
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public Boolean paramGetBoolean(String name, boolean throwExceptionWhenValueIsEmpty, boolean strConv,
            boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            }

            return false;
        }

        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        if (strConv && (o instanceof String)) {
            try {
                return Boolean.parseBoolean((String) o);
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be boolean.");
            }
        }

        throw new ParamException("Param '" + name + "' should be boolean.");
    }

    /**
     * 获取列表类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param type
     *            参数类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public <T> List<T> paramGetList(String name, boolean throwExceptionWhenValueIsEmpty, Class<?> type,
            boolean logParam) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsEmpty, throwExceptionWhenValueIsEmpty, type, logParam);
    }

    /**
     * 获取List列表类型参数
     *
     * @Description
     * @author yuy
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否必须参数
     * @param type
     * @return
     * @throws ParamException
     */
    public <T> List<T> paramGetList(String name, boolean throwExceptionWhenValueIsEmpty, Class<?> type)
            throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsEmpty, throwExceptionWhenValueIsEmpty, type, true);
    }

    public <T> List<T> paramGetList(String name, boolean throwExceptionWhenValueIsNull,
            boolean throwExceptionWhenValueIsEmpty, Class<?> type, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsNull) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return new ArrayList<T>();
            }
        }

        String str = o.toString();

        try {
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) JSON.parseArray(str, type);
            if (throwExceptionWhenValueIsEmpty && list.size() == 0) {
                throw new ParamException("Param '" + name + "', list should not be empty.");
            }
            return list;
        } catch (Exception e) {
            throw new ParamException("Param '" + name + "', list should contains " + type.getName() + " only.");
        }
    }

    /**
     * 获取列表类型参数，其元素为Long类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws ParamException
     */
    public List<Long> paramGetNumList(String name, boolean throwExceptionWhenValueIsEmpty) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsEmpty, Long.class, true);
    }

    /**
     * 获取列表类型参数，其元素为Long类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public List<Long> paramGetNumList(String name, boolean throwExceptionWhenValueIsNull,
            boolean throwExceptionWhenValueIsEmpty, boolean logParam) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, Long.class,
                logParam);
    }

    /**
     * 获取列表类型参数，其元素为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws ParamException
     */
    public List<String> paramGetStrList(String name, boolean throwExceptionWhenValueIsEmpty) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsEmpty, String.class, true);
    }

    /**
     * 获取列表类型参数，其元素为String类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public List<String> paramGetStrList(String name, boolean throwExceptionWhenValueIsNull,
            boolean throwExceptionWhenValueIsEmpty, boolean logParam) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, String.class,
                logParam);
    }

    /**
     * 获取列表类型参数，其元素为Double类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否为空
     * @return
     * @throws ParamException
     */
    public List<Double> paramGetDblList(String name, boolean throwExceptionWhenValueIsEmpty) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsEmpty, Double.class, true);
    }

    /**
     * 获取列表类型参数，其元素为Double类型 默认写日志
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty参数名
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public List<Double> paramGetDblList(String name, boolean throwExceptionWhenValueIsNull,
            boolean throwExceptionWhenValueIsEmpty, boolean logParam) throws ParamException {
        return this.paramGetList(name, throwExceptionWhenValueIsNull, throwExceptionWhenValueIsEmpty, Double.class,
                logParam);
    }

    /**
     * 获取Map类型参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @param kt
     *            Map中键的类型
     * @param vt
     *            Map中值的类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> paramGetMap(String name, boolean throwExceptionWhenValueIsEmpty, Class<K> kt, Class<V> vt,
            boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return new HashMap<K, V>();
            }
        }

        if ((o instanceof Map)) {
            Map<K, V> map = (Map<K, V>) o;
            if (throwExceptionWhenValueIsEmpty && map.size() == 0) {
                throw new ParamException("Param '" + name + "', map should not be empty.");
            }

            return map;
        } else {
            String str = o.toString();
            Map<K, V> map = null;
            try {
                map = (Map<K, V>) JSON.parseObject(str);
            } catch (Exception e) {
                if (throwExceptionWhenValueIsEmpty && map.size() == 0) {
                    throw new ParamException("Param '" + name + "', map should not be empty.");
                }
            }
            return map;
        }
    }

    /**
     * 获取Map类型参数
     *
     * @Description
     * @author yuy
     * @param name
     * @param throwExceptionWhenValueIsEmpty
     * @param vt
     * @return
     * @throws ParamException
     */
    public <V> Map<String, V> paramGetStrMap(String name, boolean throwExceptionWhenValueIsEmpty, Class<V> vt)
            throws ParamException {
        return this.paramGetMap(name, throwExceptionWhenValueIsEmpty, String.class, vt, true);
    }

    /**
     * 获取Map类型参数，其中键值对为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @return
     * @throws ParamException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean throwExceptionWhenValueIsEmpty)
            throws ParamException {
        return this.paramGetMap(name, throwExceptionWhenValueIsEmpty, String.class, Object.class, true);
    }

    /**
     * 获取Map类型参数，其中键值对为String类型
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            参数名
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean throwExceptionWhenValueIsEmpty, boolean logParam)
            throws ParamException {
        return this.paramGetMap(name, throwExceptionWhenValueIsEmpty, String.class, Object.class, logParam);
    }

    /**
     * 获取参数
     *
     * @param name
     *            参数名
     * @param throwExceptionWhenValueIsEmpty
     *            是否允许为空
     * @param type
     *            参数的类型
     * @param logParam
     *            是否写日志
     * @return
     * @throws ParamException
     */
    @SuppressWarnings("unchecked")
    public <T> T paramGetObject(String name, boolean throwExceptionWhenValueIsEmpty, Class<?> type, boolean logParam)
            throws ParamException {
        Object o = this.params.get(name);
        if (null != o && o.getClass().equals(type)) {
            return (T) o;
        }
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (throwExceptionWhenValueIsEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }

        T obj = (T) JSON.parseObject(o.toString(), type);

        return obj;
    }

    public void checkJsonParams(String... strs) throws ParamException {
        for (String str : strs) {
            if (null == this.params.get(str)) {
                throw new ParamException("Param '" + str + "' needed.");
            }
        }
    }
}
