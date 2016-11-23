package uk.gov.justice.metrics.agent.wildfly.jms;


import static uk.gov.justice.metrics.agent.wildfly.util.ReflectionUtil.invokeMethod;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.wildfly.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyJmsAgentHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyJmsAgentHelper.class);
    public static final String INTROSPECTION_ERROR = "Introspection error";

    public void onEntry(Object interceptor, Object interceptorContext) {
        collectMetrics(interceptor, interceptorContext, TimerContext::startTimer);
    }

    public void onExit(Object interceptor, Object interceptorContext) {
        collectMetrics(interceptor, interceptorContext, TimerContext::stopTimer);
    }

    private void collectMetrics(final Object interceptor, final Object interceptorContext, final BiConsumer<TimerContext, Object> timerContextOperation) {
        if (firstInterceptorInChain(interceptor)) {
            try {
                final Optional<Object> coreMessage = coreMessageFrom(interceptorContext);
                if (coreMessage.isPresent()) {
                    final Object messageId = messageIdOf(coreMessage.get()).get();
                    final TimerContext timerContext = timerContextOf(coreMessage.get());
                    timerContextOperation.accept(timerContext, messageId);
                }
            } catch (Exception e) {
                LOGGER.error(INTROSPECTION_ERROR, e);
            }
        }
    }

    private boolean firstInterceptorInChain(final Object interceptor) {
        return interceptor.getClass().getName().contains("ViewDescription");
    }

    private Optional<Object> messageIdOf(final Object coreMessage) throws ReflectiveOperationException {
        return invokeMethod(coreMessage, "getMessageID");
    }

    private TimerContext timerContextOf(final Object coreMessage) throws ReflectiveOperationException {
        final Object address = invokeMethod(coreMessage, "getAddress").get();
        final Object action = invokeMethod(coreMessage, "getStringProperty", "CPPNAME").get();
        return WildflyJmsMetricsTimerContextFactory.timerContextOf(address, action);
    }

    private Optional<Object> coreMessageFrom(final Object interceptorContext) throws ReflectiveOperationException {
        final Object parameters = invokeMethod(interceptorContext, "getParameters").get();
        Object amqMessage = ((Object[]) parameters)[0];
        return invokeMethod(amqMessage, "getCoreMessage");
    }

}