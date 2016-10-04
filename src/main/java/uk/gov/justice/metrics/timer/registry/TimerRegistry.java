package uk.gov.justice.metrics.timer.registry;

import java.util.SortedSet;

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

    public static void reset() {
        final SortedSet<String> names = METRIC_REGISTRY.getNames();
        names.forEach(METRIC_REGISTRY::remove);

    }
}
