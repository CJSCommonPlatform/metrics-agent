package uk.gov.justice.metrics.timer.context;


import static java.lang.String.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsDestinationTimerContextFactory {
    private static final Pattern QUEUE_NAME_PATTERN = Pattern.compile("^(?=.*(queue|topic))(?!.*(DLQ|ExpiryQueue)).*$");
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsDestinationTimerContextFactory.class);
    private static final Map<String, TimerContext> CONTEXT_MAP = new ConcurrentHashMap<>();
    private static final TimerContext TOTAL_TIMER_CONTEXT = new DefaultTimerContext("jms.destination.total");
    private static final EmptyTimerContext EMPTY_TIMER_CONTEXT = new EmptyTimerContext();

    public static TimerContext timerContextOf(final String destinationName, final String targetDestinationName) {
        LOGGER.trace("Fetching timer context for destination: {}, target destination: {}", destinationName, targetDestinationName);
        if (isJmsDestinationToBeMeasured(targetDestinationName)) {
            return timerContextOf(timerContextNameFrom(destinationName, targetDestinationName));
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

    private static TimerContext timerContextOf(final String timerContextName) {

        CONTEXT_MAP.computeIfAbsent(timerContextName, ctc -> new CompositeTimerContext(new DefaultTimerContext(timerContextName), TOTAL_TIMER_CONTEXT));
        return CONTEXT_MAP.get(timerContextName);
    }
}