package uk.gov.justice.metrics.timer;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class TimerRegistry {
    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    static {
        JmxReporter.forRegistry(METRIC_REGISTRY).inDomain("uk.gov.justice.metrics").build().start();
    }

    public static Timer timerOf(final String metricName) {
        return METRIC_REGISTRY.timer(metricName);
    }
}
