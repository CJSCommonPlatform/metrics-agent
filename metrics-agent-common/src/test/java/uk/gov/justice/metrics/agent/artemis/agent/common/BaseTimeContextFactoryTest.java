package uk.gov.justice.metrics.agent.artemis.agent.common;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

import org.junit.Test;

public class BaseTimeContextFactoryTest {

    private BaseTimeContextFactory baseTimeContextFactory = new BaseTimeContextFactory("total.timer.context.name.abc");


    @Test
    public void shouldReturnTimerContextByName() throws Exception {
        final TimerContext timerContext = baseTimeContextFactory.timerContextOf("tc.name.abc");

        timerContext.startTimer(1L);
        timerContext.stopTimer(1L);
        timerContext.startTimer(2L);
        timerContext.stopTimer(2L);

        assertThat(TimerRegistry.timerOf("tc.name.abc").getCount(), is(2L));
    }

    @Test
    public void shouldCreateTotalTimerContext() throws Exception {
        TimerRegistry.reset();

        final TimerContext timerContext1 = baseTimeContextFactory.timerContextOf("a");
        final TimerContext timerContext2 = baseTimeContextFactory.timerContextOf("b");
        final TimerContext timerContext3 = baseTimeContextFactory.timerContextOf("c");

        timerContext1.startTimer(1L);
        timerContext1.startTimer(2L);
        timerContext1.stopTimer(2L);
        timerContext1.stopTimer(1L);

        timerContext2.startTimer(222L);
        timerContext2.stopTimer(222L);

        timerContext3.startTimer(444L);
        timerContext3.stopTimer(444L);

        assertThat(TimerRegistry.timerOf("total.timer.context.name.abc").getCount(), is(4L));

    }

    @Test
    public void shouldReturnSameInstanceOfContextForSameArguments() throws Exception {
        assertThat(baseTimeContextFactory.timerContextOf("AAA"), sameInstance(baseTimeContextFactory.timerContextOf("AAA")));
        assertThat(baseTimeContextFactory.timerContextOf("BBB"), sameInstance(baseTimeContextFactory.timerContextOf("BBB")));
        assertThat(baseTimeContextFactory.timerContextOf("AAA"), not(sameInstance(baseTimeContextFactory.timerContextOf("BBB"))));
    }

}