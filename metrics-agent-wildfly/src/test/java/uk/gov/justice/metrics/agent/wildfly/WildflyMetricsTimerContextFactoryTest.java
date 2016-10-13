package uk.gov.justice.metrics.agent.wildfly;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;
import static uk.gov.justice.metrics.agent.wildfly.WildflyMetricsTimerContextFactory.timerContextOf;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.TimerRegistry;

import org.junit.Test;

public class WildflyMetricsTimerContextFactoryTest {

    @Test
    public void shouldCreateTimerContextForJmsQueue() throws Exception {

        final String queueName = "jms.queue.abc";
        final String actionName = "context.action";

        final TimerContext timerContext = timerContextOf(queueName, actionName);
        timerContext.startTimer("1");
        timerContext.stopTimer("1");
        timerContext.startTimer("2");
        timerContext.stopTimer("2");
        assertThat(TimerRegistry.timerOf("wildfly.jms.queue.abc-context.action").getCount(), is(2L));

    }

    @Test
    public void shouldReturnSameInstanceOfContextForSameArguments() throws Exception {
        assertThat(timerContextOf("jms.queue.aaa", "context.action"), sameInstance(timerContextOf("jms.queue.aaa", "context.action")));
        assertThat(timerContextOf("jms.topic.aaa", "context.action"), sameInstance(timerContextOf("jms.topic.aaa", "context.action")));
        assertThat(timerContextOf("jms.queue.aaa", "context.action"), not(sameInstance(timerContextOf("jms.topic.aaa", "context.action"))));
    }


}