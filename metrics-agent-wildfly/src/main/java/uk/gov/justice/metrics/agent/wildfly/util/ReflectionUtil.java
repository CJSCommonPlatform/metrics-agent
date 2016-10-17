package uk.gov.justice.metrics.agent.wildfly.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
    public static Object invokeMethod(final Object obj, final String methodName, final Object... param) throws ReflectiveOperationException {
        Class<?> clazz = obj.getClass();
        for (int i = 0; i < 3; i++) {
            try {
                final Method method = param.length == 0 ? clazz.getDeclaredMethod(methodName) : clazz.getDeclaredMethod(methodName, param[0].getClass());
                return method.invoke(obj, param);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw e;
            } catch (NoSuchMethodException e) {
                final Class<?> superclass = clazz.getSuperclass();
                if (superclass != null) {
                    clazz = superclass;
                } else {
                    throw e;
                }
            }
        }
        return null;
    }
}