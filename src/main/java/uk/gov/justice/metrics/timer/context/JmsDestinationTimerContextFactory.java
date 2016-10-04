package uk.gov.justice.metrics.timer.context;


import static java.lang.String.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JmsDestinationTimerContextFactory {
    private static final Map<String, TimerContext> CONTEXT_MAP = new ConcurrentHashMap<>();
    private static final TimerContext TOTAL_TIMER_CONTEXT = new DefaultTimerContext("jms.destination.total");
    private static final EmptyTimerContext EMPTY_TIMER_CONTEXT = new EmptyTimerContext();

    public static TimerContext timerContextOf(final String destinationName, final String targetDestinationName) {
        if (isJmsDestinationToBeMeasured(targetDestinationName)) {
            return timerContextOf(timerContextNameFrom(destinationName, targetDestinationName));
        } else {
            return EMPTY_TIMER_CONTEXT;
        }
    }

    private static boolean isJmsDestinationToBeMeasured(final String targetQueueName) {
        return targetQueueName.contains("queue") || targetQueueName.contains("topic");
    }

    private static String timerContextNameFrom(final String destinationName, final String targetDestinationName) {
        return destinationName.equals(targetDestinationName) ? targetDestinationName : format("cluster.bridge.for.%s", targetDestinationName);
    }

    private static TimerContext timerContextOf(final String timerContextName) {
        CONTEXT_MAP.putIfAbsent(timerContextName, new CompositeTimerContext(new DefaultTimerContext(timerContextName), TOTAL_TIMER_CONTEXT));
        return CONTEXT_MAP.get(timerContextName);
    }
}