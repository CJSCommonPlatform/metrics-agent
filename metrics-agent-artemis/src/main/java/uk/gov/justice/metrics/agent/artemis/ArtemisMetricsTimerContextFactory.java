package uk.gov.justice.metrics.agent.artemis;


import static java.lang.String.format;

import uk.gov.justice.metrics.agent.artemis.agent.common.EmptyTimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.BaseTimeContextFactory;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisMetricsTimerContextFactory {
    private static final Pattern QUEUE_NAME_PATTERN = Pattern.compile("^(?=.*(queue|topic))(?!.*(DLQ|ExpiryQueue)).*$");
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtemisMetricsTimerContextFactory.class);
    private static final EmptyTimerContext EMPTY_TIMER_CONTEXT = new EmptyTimerContext();
    private static final BaseTimeContextFactory BASE_TIME_CONTEXT_FACTORY = new BaseTimeContextFactory("jms.destination.total");



    public static TimerContext timerContextOf(final String destinationName, final String targetDestinationName) {
        LOGGER.trace("Fetching timer context for destination: {}, target destination: {}", destinationName, targetDestinationName);
        if (isJmsDestinationToBeMeasured(targetDestinationName)) {
            return BASE_TIME_CONTEXT_FACTORY.timerContextOf(timerContextNameFrom(destinationName, targetDestinationName));
        } else {
            return EMPTY_TIMER_CONTEXT;
        }
    }

    private static boolean isJmsDestinationToBeMeasured(final String targetQueueName) {
        return QUEUE_NAME_PATTERN.matcher(targetQueueName).matches();
    }

    private static String timerContextNameFrom(final String destinationName, final String targetDestinationName) {
        return destinationName.equals(targetDestinationName) ? targetDestinationName : format("%s-%s", targetDestinationName, destinationName);
    }

}