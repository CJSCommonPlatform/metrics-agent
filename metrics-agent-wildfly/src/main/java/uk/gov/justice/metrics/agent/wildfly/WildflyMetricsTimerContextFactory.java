package uk.gov.justice.metrics.agent.wildfly;

import static java.lang.String.format;

import uk.gov.justice.metrics.agent.artemis.agent.common.CompositeTimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.DefaultTimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyMetricsTimerContextFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyMetricsTimerContextFactory.class);
    private static final Map<String, TimerContext> CONTEXT_MAP = new ConcurrentHashMap<>();

    public static TimerContext timerContextOf(final Object queueName, final Object actionName) {
        LOGGER.trace("Fetching timer context for destination: {}, actionName: {}", queueName, actionName);
        return timerContextOf(timerContextNameFrom(String.valueOf(queueName), String.valueOf(actionName)));
    }

    private static String timerContextNameFrom(final String queueName, final String actionName) {
        return format("wildfly.%s-%s", queueName, actionName);
    }

    private static TimerContext timerContextOf(final String timerContextName) {
        CONTEXT_MAP.computeIfAbsent(timerContextName, tc -> new DefaultTimerContext(timerContextName));
        return CONTEXT_MAP.get(timerContextName);
    }
}
