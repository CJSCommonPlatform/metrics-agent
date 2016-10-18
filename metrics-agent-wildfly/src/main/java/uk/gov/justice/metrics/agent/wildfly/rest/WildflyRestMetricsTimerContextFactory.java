package uk.gov.justice.metrics.agent.wildfly.rest;

import static java.lang.String.format;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.BaseTimeContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRestMetricsTimerContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyRestMetricsTimerContextFactory.class);
    private static final BaseTimeContextFactory BASE_TIME_CONTEXT_FACTORY = new BaseTimeContextFactory("wildfly.rest.total");
    private static final String URL_PARTS_TO_REMOVE = "/query|/command|/api|/view|/controller|/rest|/$|^/";
    private static final String ID_PATTERN = "/[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}|/[0-9][A-Za-z0-9]*";


    public static TimerContext timerContextOf(final String requestURL) {
        LOGGER.trace("Fetching timer context for rest request: {}", requestURL);
        return BASE_TIME_CONTEXT_FACTORY.timerContextOf(timerContextNameFrom(requestURL));
    }

    static String timerContextNameFrom(final String requestURL) {
        return format("wildfly.rest.%s", requestURL.replaceAll(URL_PARTS_TO_REMOVE, "")).replaceAll(ID_PATTERN, "/{id}");
    }
}
