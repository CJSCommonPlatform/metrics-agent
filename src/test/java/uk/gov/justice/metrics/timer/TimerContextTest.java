package uk.gov.justice.metrics.timer;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TimerContextTest {

    @Test
    public void shouldStartAndStopTimerById() throws Exception {

        final TimerContext context = new TimerContext("someMetricsABC");
        context.startTimer("123");
        context.stopTimer("123");

        context.startTimer("456");
        context.stopTimer("456");

        assertThat(TimerRegistry.timerOf("someMetricsABC").getCount(), is(2L));
    }

    @Test
    public void shouldNotStopTimerIfIdDoesNotmatch() throws Exception {

        final TimerContext context = new TimerContext("someMetricsBcd");
        context.startTimer("123");
        context.stopTimer("124");

        assertThat(TimerRegistry.timerOf("someMetricsABC").getCount(), is(0L));

    }

    @Test
    public void shouldRemoveStoppedTimerFromContext() throws Exception {

        final TimerContext context = new TimerContext("someMetricsEFG");
        context.startTimer("123");
        context.stopTimer("123");

        context.startTimer("123");
        context.stopTimer("123");

        assertThat(TimerRegistry.timerOf("someMetricsABC").getCount(), is(2L));
    }

    @Test
    public void shouldCollectIndividualStatisticsForDifferentMetrics() throws Exception {

        final TimerContext context1 = new TimerContext("someMetrics1");
        context1.startTimer("123");
        context1.stopTimer("123");
        assertThat(TimerRegistry.timerOf("someMetrics1").getCount(), is(1L));

        final TimerContext context2 = new TimerContext("someMetrics2");
        context2.startTimer("123");
        context2.stopTimer("123");

        assertThat(TimerRegistry.timerOf("someMetrics2").getCount(), is(1L));
    }
}