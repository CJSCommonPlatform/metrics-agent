package uk.gov.justice.metrics.timer.context;

import uk.gov.justice.metrics.timer.registry.TimerRegistry;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTimerContext implements TimerContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTimerContext.class);
    private final String name;
    private Map<String, Timer.Context> tickingTimers = ExpiringMap.builder()
            .expiration(90, TimeUnit.SECONDS)
            .build();

    public DefaultTimerContext(final String name) {
        this.name = name;
        LOGGER.trace("Creating timer context for metrics {}", name);
    }

    @Override
    public void startTimer(final String timerId) {
        LOGGER.trace("Starting timer for metrics {}, timerId: {}", name, timerId);
        tickingTimers.put(timerId, TimerRegistry.timerOf(name).time());
    }

    @Override
    public void stopTimer(final String timerId) {
        final Timer.Context tickingTimer = tickingTimers.remove(timerId);
        if (tickingTimer != null) {
            LOGGER.trace("Stopping timer for metrics {}, timerId: {}", name, timerId);
            tickingTimer.stop();
        }
    }
}