package uk.gov.justice.metrics.agent;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.metrics.agent.ArtemisAgent.instrumentedByteCodeOf;

import uk.gov.justice.metrics.timer.registry.TimerRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.server.Consumer;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArtemisAgentTest {

    private static final String QUEUE_CLASS_NAME = "org.apache.activemq.artemis.core.server.impl.QueueImpl";
    private static Class<?> QUEUE_CLASS;
    private static Method ADD_TAIL_METHOD;
    private static Method PROCEED_DELIVER_METHOD;

    static {
        try {
            QUEUE_CLASS = new TransformingClassLoader(QUEUE_CLASS_NAME).loadClass(QUEUE_CLASS_NAME);
            ADD_TAIL_METHOD = QUEUE_CLASS.getMethod("addTail", MessageReference.class, boolean.class);
            PROCEED_DELIVER_METHOD = QUEUE_CLASS.getMethod("proceedDeliver", Consumer.class, MessageReference.class);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Before
    public void setUp() throws Exception {
        TimerRegistry.reset();
    }

    @Test
    public void shouldCollectMetricsForAJmsQueue() throws Exception {

        final String queueName = "jms.queue.queueNameABC";
        final Object queueInstance = newQueueInstance(queueName);

        final MessageReference message1 = messageOf(1l, queueName);
        addToQueue(queueInstance, message1);
        final MessageReference message2 = messageOf(2l, queueName);
        addToQueue(queueInstance, message2);
        final MessageReference message3 = messageOf(3l, queueName);
        addToQueue(queueInstance, message3);

        deliver(queueInstance, message1);
        deliver(queueInstance, message2);

        assertThat(TimerRegistry.timerOf(queueName).getCount(), is(2L));

    }

    @Test
    public void shouldCollectTotalMetrics() throws Exception {

        final String queueNameA = "jms.queue.queueNameA";
        final Object queueAInstance = newQueueInstance(queueNameA);

        final String queueNameB = "jms.queue.queueNameB";
        final Object queueBInstance = newQueueInstance(queueNameA);


        final MessageReference message1 = messageOf(1l, queueNameA);
        addToQueue(queueAInstance, message1);

        final MessageReference message2 = messageOf(2l, queueNameA);
        addToQueue(queueAInstance, message2);

        final MessageReference message3 = messageOf(3l, queueNameB);
        addToQueue(queueBInstance, message3);

        deliver(queueAInstance, message1);
        deliver(queueAInstance, message2);
        deliver(queueBInstance, message3);

        assertThat(TimerRegistry.timerOf("jms.destination.total").getCount(), is(3L));

    }

    @Test
    public void shouldCollectMetricsForAClusterBridge() throws Exception {
        final Object clusterBridgeQueue = newQueueInstance("myCluster-blah123");

        final MessageReference message1 = messageOf(1l, "jms.queue.targetQueueABC");
        addToQueue(clusterBridgeQueue, message1);
        deliver(clusterBridgeQueue, message1);

        assertThat(TimerRegistry.timerOf("jms.queue.targetQueueABC-myCluster-blah123").getCount(), is(1L));
    }


    private void deliver(final Object queueInstance, final MessageReference message) throws IllegalAccessException, InvocationTargetException {
        PROCEED_DELIVER_METHOD.invoke(queueInstance, new Consumer(), message);
    }

    private void addToQueue(final Object queueInstance, final MessageReference messageReference) throws IllegalAccessException, InvocationTargetException {
        ADD_TAIL_METHOD.invoke(queueInstance, messageReference, false);
    }

    private MessageReference messageOf(final long messageID, final String address) {
        return new MessageReference(messageID, new SimpleString(address));
    }

    private Object newQueueInstance(final String destinationName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Constructor<?> constructor = QUEUE_CLASS.getConstructor(SimpleString.class);
        return constructor.newInstance(new SimpleString(destinationName));
    }

    private static class TransformingClassLoader extends ClassLoader {

        private final String className;

        public TransformingClassLoader(String className) {
            super();
            this.className = className;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals(className)) {
                byte[] byteBuffer = instrumentedByteCodeOf(className.replace('.', '/'));
                return defineClass(className, byteBuffer, 0, byteBuffer.length);
            }
            return super.loadClass(name);
        }
    }
}