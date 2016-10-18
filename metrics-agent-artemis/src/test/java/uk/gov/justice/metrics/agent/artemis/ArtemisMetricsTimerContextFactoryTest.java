package uk.gov.justice.metrics.agent.artemis;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.metrics.agent.artemis.ArtemisMetricsTimerContextFactory.timerContextOf;


import uk.gov.justice.metrics.agent.artemis.agent.common.TimerContext;
import uk.gov.justice.metrics.agent.artemis.agent.common.TimerRegistry;

import org.junit.Test;


public class ArtemisMetricsTimerContextFactoryTest {

    @Test
    public void shouldCreateTimerContextForJmsQueue() throws Exception {

        final String queueName = "jms.queue.abc";
        final String targetQueueName = "jms.queue.abc";

        final TimerContext timerContext = timerContextOf(queueName, targetQueueName);
        timerContext.startTimer("1");
        timerContext.stopTimer("1");
        timerContext.startTimer("2");
        timerContext.stopTimer("2");
        assertThat(TimerRegistry.timerOf("jms.queue.abc").getCount(), is(2L));

    }

    @Test
    public void shouldCreateTimerContextForClusterBridge() throws Exception {

        final String queueName = "my-cluster-aaaa123";
        final String targetQueueName = "jms.queue.abc";

        final TimerContext timerContext = timerContextOf(queueName, targetQueueName);
        timerContext.startTimer("1");
        timerContext.stopTimer("1");
        assertThat(TimerRegistry.timerOf("jms.queue.abc-my-cluster-aaaa123").getCount(), is(1L));
    }

    @Test
    public void shouldReturnSameInstanceOfContextForSameArguments() throws Exception {
        assertThat(timerContextOf("jms.queue.aaa", "jms.queue.aaa"), sameInstance(timerContextOf("jms.queue.aaa", "jms.queue.aaa")));
        assertThat(timerContextOf("jms.topic.aaa", "jms.topic.aaa"), sameInstance(timerContextOf("jms.topic.aaa", "jms.topic.aaa")));
        assertThat(timerContextOf("jms.queue.aaa", "jms.queue.aaa"), not(sameInstance(timerContextOf("jms.queue.bbb", "jms.queue.aaa"))));
    }

    @Test
    public void shouldCreateTotalTimerContext() throws Exception {
        TimerRegistry.reset();

        final TimerContext timerContextAAA = timerContextOf("jms.queue.aaa", "jms.queue.aaa");
        timerContextAAA.startTimer("123");
        timerContextAAA.stopTimer("123");

        timerContextAAA.startTimer("234");
        timerContextAAA.stopTimer("234");

        final TimerContext timerContextBBB = timerContextOf("jms.queue.bbb", "jms.queue.bbb");
        timerContextBBB.startTimer("111");
        timerContextBBB.stopTimer("111");

        final TimerContext timerContextCluster = timerContextOf("my-cluster-bbb", "jms.queue.bbb");
        timerContextCluster.startTimer("4444");
        timerContextCluster.stopTimer("4444");

        assertThat(TimerRegistry.timerOf("jms.destination.total").getCount(), is(4L));
    }

    @Test
    public void shouldNotCollectMeasurmentsWhenTargetDestNameDoesNotContainWordsQueueOrTopic() throws Exception {
        TimerRegistry.reset();

        final TimerContext timerContextAAA = timerContextOf("aaa", "aaa");
        timerContextAAA.startTimer("123");
        timerContextAAA.stopTimer("123");

        assertThat(TimerRegistry.timerOf("jms.destination.total").getCount(), is(0L));
        assertThat(TimerRegistry.timerOf("aaa").getCount(), is(0L));
    }


    @Test
    public void shouldNotCollectMeasurmentsDLQ() throws Exception {
        TimerRegistry.reset();

        final TimerContext timerContextAAA = timerContextOf("jms.queue.DLQ", "jms.queue.DLQ");
        timerContextAAA.startTimer("123");
        timerContextAAA.stopTimer("123");

        assertThat(TimerRegistry.timerOf("jms.destination.total").getCount(), is(0L));
        assertThat(TimerRegistry.timerOf("aaa").getCount(), is(0L));
    }

}