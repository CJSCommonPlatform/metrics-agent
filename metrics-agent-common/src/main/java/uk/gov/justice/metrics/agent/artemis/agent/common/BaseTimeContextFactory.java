package uk.gov.justice.metrics.agent.artemis.agent.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class BaseTimeContextFactory {
    private final Map<String, TimerContext> contextMap = new ConcurrentHashMap<>();
    private final TimerContext totalTimerContext;

    public BaseTimeContextFactory(final String totalTimeContextName) {
        totalTimerContext = new DefaultTimerContext(totalTimeContextName);
    }

    public TimerContext timerContextOf(final String timerContextName) {
        contextMap.computeIfAbsent(timerContextName, tc -> new CompositeTimerContext(new DefaultTimerContext(timerContextName), totalTimerContext));
        return contextMap.get(timerContextName);
    }
}
