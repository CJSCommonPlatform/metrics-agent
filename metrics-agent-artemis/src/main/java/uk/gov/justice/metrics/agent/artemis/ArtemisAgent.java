package uk.gov.justice.metrics.agent.artemis;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisAgent {

    private static final String INTROSPECTED_CLASS = "org/apache/activemq/artemis/core/server/impl/QueueImpl";
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtemisAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer((classLoader, className, aClass, protectionDomain, classFileBuffer) -> instrumentedByteCodeOf(className));
    }

    static byte[] instrumentedByteCodeOf(final String className) {
        if (INTROSPECTED_CLASS.equals(className)) {
            LOGGER.info("Introspecting class {}", className);
            try {
                ClassPool classPool = classPool();
                CtClass queueClass = classPool.get(INTROSPECTED_CLASS.replace('/', '.'));
                addTimerStartingCode(classPool, queueClass);
                addTimerStoppingCode(classPool, queueClass);
                return byteCodeOf(queueClass);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to add metrics agent", ex);
            }
        }
        return null;
    }

    private static void addTimerStartingCode(final ClassPool classPool, final CtClass queueClass) throws NotFoundException, CannotCompileException {
        CtMethod addTailMethod = queueClass.getDeclaredMethod("addTail", new CtClass[]{classPool.get("org.apache.activemq.artemis.core.server.MessageReference"), CtClass.booleanType});
        addTailMethod.insertBefore("uk.gov.justice.metrics.agent.artemis.ArtemisMetricsTimerContextFactory.timerContextOf(name.toString(), ref.getMessage().getAddress().toString()).startTimer(Long.valueOf(ref.getMessage().getMessageID()));");
    }

    private static void addTimerStoppingCode(final ClassPool classPool, final CtClass queueClass) throws NotFoundException, CannotCompileException {
        CtMethod proceedDeliver = queueClass.getDeclaredMethod("proceedDeliver", new CtClass[]{classPool.get("org.apache.activemq.artemis.core.server.Consumer"), classPool.get("org.apache.activemq.artemis.core.server.MessageReference")});
        proceedDeliver.insertAfter("uk.gov.justice.metrics.agent.artemis.ArtemisMetricsTimerContextFactory.timerContextOf(name.toString(), reference.getMessage().getAddress().toString()).stopTimer(Long.valueOf(reference.getMessage().getMessageID()));");
    }

    private static byte[] byteCodeOf(final CtClass queueClass) throws IOException, CannotCompileException {
        byte[] byteCode = queueClass.toBytecode();
        queueClass.detach();
        return byteCode;
    }

    private static ClassPool classPool() {
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        return pool;
    }
}