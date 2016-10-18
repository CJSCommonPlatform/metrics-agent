package uk.gov.justice.metrics.agent.wildfly.jms;

import static java.lang.String.format;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.BaseTimeContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyJmsMetricsTimerContextFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyJmsMetricsTimerContextFactory.class);
    private static final BaseTimeContextFactory BASE_TIME_CONTEXT_FACTORY = new BaseTimeContextFactory("wildfly.jms.total");

    public static TimerContext timerContextOf(final Object queueName, final Object actionName) {
        LOGGER.trace("Fetching timer context for jms destination: {}, actionName: {}", queueName, actionName);
        return BASE_TIME_CONTEXT_FACTORY.timerContextOf(timerContextNameFrom(String.valueOf(queueName), String.valueOf(actionName)));
    }

    private static String timerContextNameFrom(final String queueName, final String actionName) {
        return format("wildfly.%s-%s", queueName, actionName);
    }

}
