package uk.gov.justice.metrics.agent.wildfly;


import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyAgentHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyAgentHelper.class);
    public static final String INTROSPECTION_ERROR = "Introspection error";

    public void onEntry(Object interceptor, Object interceptorContext) {
        if (firstInterceptorInChain(interceptor)) {

            try {
                final Object coreMessage = coreMessageFrom(interceptorContext);
                final TimerContext timerContext = timerContextOf(coreMessage);
                final Object messageId = messageIdOf(coreMessage);
                timerContext.startTimer(messageId);
            } catch (ReflectiveOperationException e) {
                LOGGER.error(INTROSPECTION_ERROR, e);
            }
        }
    }


    public void onExit(Object interceptor, Object interceptorContext) {
        if (firstInterceptorInChain(interceptor)) {
            Object coreMessage = null;
            try {
                coreMessage = coreMessageFrom(interceptorContext);
                final Object messageId = messageIdOf(coreMessage);
                final TimerContext timerContext = timerContextOf(coreMessage);
                timerContext.stopTimer(messageId);
            } catch (ReflectiveOperationException e) {
                LOGGER.error(INTROSPECTION_ERROR, e);
            }
        }
    }

    private boolean firstInterceptorInChain(final Object interceptor) {
        return interceptor.getClass().getName().contains("ViewDescription");
    }

    private Object messageIdOf(final Object coreMessage) throws ReflectiveOperationException {
        return invokeMethod(coreMessage, "getMessageID");
    }

    private TimerContext timerContextOf(final Object coreMessage) throws ReflectiveOperationException {
        final Object address = invokeMethod(coreMessage, "getAddress");
        final Object action = invokeMethod(coreMessage, "getStringProperty", "CPPNAME");
        return WildflyMetricsTimerContextFactory.timerContextOf(address, action);
    }

    private Object coreMessageFrom(final Object interceptorContext) throws ReflectiveOperationException {
        final Object parameters = invokeMethod(interceptorContext, "getParameters");
        Object amqMessage = ((Object[]) parameters)[0];
        return invokeMethod(amqMessage, "getCoreMessage");
    }

    private Object invokeMethod(final Object obj, final String methodName, final Object... param) throws ReflectiveOperationException {
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