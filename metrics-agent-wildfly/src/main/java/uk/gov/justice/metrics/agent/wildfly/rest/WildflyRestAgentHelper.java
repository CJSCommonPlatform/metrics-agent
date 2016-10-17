package uk.gov.justice.metrics.agent.wildfly.rest;


import static uk.gov.justice.metrics.agent.wildfly.rest.WildflyRestMetricsTimerContextFactory.timerContextOf;
import static uk.gov.justice.metrics.agent.wildfly.util.ReflectionUtil.invokeMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRestAgentHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyRestAgentHelper.class);

    public static final String INTROSPECTION_ERROR = "Introspection error";

    public void onEntry(final Object serverExchange) {
        try {
            timerContextOf(requestPathFrom(serverExchange)).startTimer(serverExchange.hashCode());

        } catch (ReflectiveOperationException e) {
            LOGGER.error(INTROSPECTION_ERROR, e);
        }

    }

    public void onExit(final Object serverExchange) {
        try {
            timerContextOf(requestPathFrom(serverExchange)).stopTimer(serverExchange.hashCode());
        } catch (ReflectiveOperationException e) {
            LOGGER.error(INTROSPECTION_ERROR, e);
        }
    }


    private String requestPathFrom(final Object serverExchange) throws ReflectiveOperationException {
        return (String) invokeMethod(serverExchange, "getRequestPath");
    }
}