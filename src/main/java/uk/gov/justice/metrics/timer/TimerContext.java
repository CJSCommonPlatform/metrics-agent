package uk.gov.justice.metrics.timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimerContext.class);
    private final Timer timer;
    private final String metricName;
    private Map<String, Timer.Context> tickingTimers = new ConcurrentHashMap<>();

    public TimerContext(final String metricName) {
        this.metricName = metricName;
        LOGGER.trace("Creating timer context for metrics {}", metricName);
        this.timer = TimerRegistry.timerOf(metricName);
    }

    public void startTimer(final String timerId) {
        LOGGER.trace("Starting timer for metrics {}, timerId:", metricName, timerId);
        tickingTimers.put(timerId, timer.time());
    }

    public void stopTimer(final String timerId) {
        final Timer.Context tickingTimer = tickingTimers.remove(timerId);
        if (tickingTimer != null) {
            LOGGER.trace("Stopping timer for metrics {}, timerId:", metricName, timerId);
            tickingTimer.stop();
        }
    }
}