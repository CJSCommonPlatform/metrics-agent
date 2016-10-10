package uk.gov.justice.metrics.agent.artemis.agent.common;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CompositeTimerContextTest {

    @Mock
    private TimerContext timerContext1;

    @Mock
    private TimerContext timerContext2;

    private TimerContext compositeTimerContext;


    @Before
    public void setUp() throws Exception {
        compositeTimerContext = new CompositeTimerContext(timerContext1, timerContext2);

    }

    @Test
    public void shouldStartTimerOnBothComposedContexts() throws Exception {
        compositeTimerContext.startTimer("111");

        verify(timerContext1).startTimer("111");
        verify(timerContext2).startTimer("111");
    }

    @Test
    public void shouldStopTimerOnBothComposedContexts() throws Exception {
        compositeTimerContext.stopTimer("112");

        verify(timerContext1).stopTimer("112");
        verify(timerContext2).stopTimer("112");
    }
}