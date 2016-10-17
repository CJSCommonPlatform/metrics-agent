package uk.gov.justice.metrics.agent.wildfly.jms;


import static uk.gov.justice.metrics.agent.wildfly.util.ReflectionUtil.invokeMethod;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.wildfly.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyJmsAgentHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyJmsAgentHelper.class);
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
            try {
                final Object coreMessage = coreMessageFrom(interceptorContext);
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
        return WildflyJmsMetricsTimerContextFactory.timerContextOf(address, action);
    }

    private Object coreMessageFrom(final Object interceptorContext) throws ReflectiveOperationException {
        final Object parameters = invokeMethod(interceptorContext, "getParameters");
        Object amqMessage = ((Object[]) parameters)[0];
        return invokeMethod(amqMessage, "getCoreMessage");
    }

}