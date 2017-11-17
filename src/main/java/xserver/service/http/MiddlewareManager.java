package xserver.service.http;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MiddlewareManager {
    private static MiddlewareManager middleware;

    public Map<String, Middleware> middlewareMapping;

    public static MiddlewareManager getInst() {
        if (null == MiddlewareManager.middleware) {
            synchronized (MiddlewareManager.class) {
                if (null == MiddlewareManager.middleware) {
                    MiddlewareManager.middleware = new MiddlewareManager();
                }
            }
        }
        return MiddlewareManager.middleware;
    }

    private MiddlewareManager() {
        this.middlewareMapping = new HashMap<String, Middleware>();
    }

    public void regist(Object middlewareObj) {
        Class<?> clazz = middlewareObj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            MiddlewareMethod middlewareAnno = method.getDeclaredAnnotation(MiddlewareMethod.class);

            if (null == middlewareAnno) {
                continue;
            }

            Middleware middlewareInfo = new Middleware();
            String middlewareName = middlewareAnno.value();
            if (null == middlewareName || 0 == middlewareName.length()) {
                middlewareName = middlewareObj.getClass().getName() + "." + method.getName();
            }
            middlewareInfo.method = method;
            middlewareInfo.middleObj = middlewareObj;
            this.middlewareMapping.put(middlewareName, middlewareInfo);
        }
    }

    public Middleware getMiddleware(String middlewareStr) {
        return this.middlewareMapping.get(middlewareStr);
    }
}
