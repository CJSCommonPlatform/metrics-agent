package uk.gov.justice.metrics.agent.wildfly;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.metrics.agent.artemis.agent.common.TimerRegistry;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;

import org.apache.activemq.artemis.core.client.impl.TestClientMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.jboss.as.ee.component.ViewDescription;
import org.jboss.invocation.InterceptorContext;
import org.junit.Before;
import org.junit.Test;

public class WildflyAgentHelperTest {

    public static final ViewDescription INITIAL_INTERCEPTOR = new ViewDescription();
    WildflyAgentHelper agentHelper = new WildflyAgentHelper();

    @Before
    public void setUp() throws Exception {
        TimerRegistry.reset();
    }

    @Test
    public void shouldCollectMetricsForMessages() throws Exception {

        final Message message = new ActiveMQTextMessage(new TestClientMessage(123, "jms.queue.abc", "contextname.actionname"), null);
        final Message message2 = new ActiveMQTextMessage(new TestClientMessage(124, "jms.queue.abc", "contextname.actionname"), null);
        final Message messageDifferentQueue = new ActiveMQTextMessage(new TestClientMessage(125, "jms.queue.bcd", "contextname.actionname"), null);
        final Message messageDifferentAction = new ActiveMQTextMessage(new TestClientMessage(126, "jms.queue.abc", "contextname.actionname2"), null);

        agentHelper.onEntry(INITIAL_INTERCEPTOR, new InterceptorContext(message));
        agentHelper.onEntry(INITIAL_INTERCEPTOR, new InterceptorContext(message2));
        agentHelper.onEntry(INITIAL_INTERCEPTOR, new InterceptorContext(messageDifferentQueue));
        agentHelper.onEntry(INITIAL_INTERCEPTOR, new InterceptorContext(messageDifferentAction));

        agentHelper.onExit(INITIAL_INTERCEPTOR, new InterceptorContext(message2));
        agentHelper.onExit(INITIAL_INTERCEPTOR, new InterceptorContext(message));
        agentHelper.onExit(INITIAL_INTERCEPTOR, new InterceptorContext(messageDifferentAction));
        agentHelper.onExit(INITIAL_INTERCEPTOR, new InterceptorContext(messageDifferentQueue));

        assertThat(TimerRegistry.timerOf("wildfly.jms.queue.abc-contextname.actionname").getCount(), is(2L));
        assertThat(TimerRegistry.timerOf("wildfly.jms.queue.bcd-contextname.actionname").getCount(), is(1L));
        assertThat(TimerRegistry.timerOf("wildfly.jms.queue.abc-contextname.actionname2").getCount(), is(1L));
    }

    @Test
    public void shouldNotCollectMetricsIfNotIntialInterceptor() {
        final Message message = new ActiveMQTextMessage(new TestClientMessage(127, "jms.queue.abc", "contextname.actionname"), null);
        agentHelper.onEntry(new Object(), new InterceptorContext(message));
        agentHelper.onExit(new Object(), new InterceptorContext(message));
        assertThat(TimerRegistry.timerOf("wildfly.jms.queue.abc-contextname.actionname").getCount(), is(0L));

    }

    @Test
    public void shouldLogErrorInCaseExcpectedMethodNotFoundInPassedObject() {
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);

        agentHelper.onEntry(INITIAL_INTERCEPTOR, new Object());

        logger.removeAppender(appender);
        final List<LoggingEvent> log = appender.messages;
        final LoggingEvent logEntry = log.get(0);
        assertThat(logEntry.getLevel(), is(Level.ERROR));
        assertThat((String) logEntry.getMessage(), containsString("Introspection error"));
    }

    private static class TestAppender extends AppenderSkeleton {
        private List<LoggingEvent> messages = new ArrayList<LoggingEvent>();

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            messages.add(loggingEvent);
        }

        @Override
        public void close() {

        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}