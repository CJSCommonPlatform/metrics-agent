package uk.gov.justice.metrics.agent;

import static java.lang.String.format;
import static javassist.Modifier.STATIC;

import uk.gov.justice.metrics.timer.TimerContext;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisAgent {

    private static final String INTROSPECTED_CLASS = "org/apache/activemq/artemis/core/server/impl/QueueImpl";
    private static final String TOTAL_TIMER_CONTEXT_VARIABLE_NAME = "TOTAL_TIMER_CONTEXT";
    private static final String QUEUE_TIMER_CONTEXT_VARIABLE_NAME = "queueTimerContext";
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtemisAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer((classLoader, className, aClass, protectionDomain, classfileBuffer) -> {
            if (INTROSPECTED_CLASS.equals(className)) {
                LOGGER.info("Introspecting class {}", className);
                try {
                    ClassPool classPool = classPool();

                    CtClass queueClass = classPool.get(INTROSPECTED_CLASS.replace('/', '.'));

                    addTotalTimerContextInstatiationCode(classPool, queueClass);

                    addQueueTimerContextInstantiationCode(classPool, queueClass);

                    addTimerStartingCode(classPool, queueClass);

                    addTimerStoppingCode(queueClass);

                    return bytecodeOf(queueClass);
                } catch (Exception ex) {
                    throw new IllegalStateException("Failed to add metrics agent", ex);
                }
            }

            return null;
        });
    }

    private static void addTimerStartingCode(final ClassPool classPool, final CtClass queueClass) throws NotFoundException, CannotCompileException {
        CtMethod addTailMethod = queueClass.getDeclaredMethod("addTail", new CtClass[]{classPool.get("org.apache.activemq.artemis.core.server.MessageReference"), CtClass.booleanType});
        final String startTimerMethodInvocation = ".startTimer(String.valueOf(ref.getMessage().getMessageID()));";
        addTailMethod.insertBefore(format("%s%s%s%s", TOTAL_TIMER_CONTEXT_VARIABLE_NAME, startTimerMethodInvocation, QUEUE_TIMER_CONTEXT_VARIABLE_NAME, startTimerMethodInvocation));
    }

    private static void addTimerStoppingCode(final CtClass queueClass) throws NotFoundException, CannotCompileException {
        CtMethod proceedDeliver = queueClass.getDeclaredMethod("proceedDeliver");
        final String stopTimerMethodInvocation = ".stopTimer(String.valueOf(reference.getMessage().getMessageID()));";
        proceedDeliver.insertAfter(format("%s%s%s%s", TOTAL_TIMER_CONTEXT_VARIABLE_NAME, stopTimerMethodInvocation, QUEUE_TIMER_CONTEXT_VARIABLE_NAME, stopTimerMethodInvocation));
    }

    private static byte[] bytecodeOf(final CtClass queueClass) throws IOException, CannotCompileException {
        byte[] byteCode = queueClass.toBytecode();
        queueClass.detach();
        return byteCode;
    }

    private static void addQueueTimerContextInstantiationCode(final ClassPool pool, final CtClass queueClass) throws CannotCompileException, NotFoundException {
        queueClass.addField(new CtField(pool.get(TimerContext.class.getName()), QUEUE_TIMER_CONTEXT_VARIABLE_NAME, queueClass));
        final CtConstructor[] declaredConstructors = queueClass.getDeclaredConstructors();
        for (CtConstructor declaredConstructor : declaredConstructors) {
            declaredConstructor.insertAfter(format("%s = new %s(name.toString());", QUEUE_TIMER_CONTEXT_VARIABLE_NAME, TimerContext.class.getName()));
        }
    }

    private static void addTotalTimerContextInstatiationCode(final ClassPool pool, final CtClass queueClass) throws CannotCompileException, NotFoundException {
        CtField totalTimerContextField = new CtField(pool.get(TimerContext.class.getName()), TOTAL_TIMER_CONTEXT_VARIABLE_NAME, queueClass);
        totalTimerContextField.setModifiers(STATIC);
        queueClass.addField(totalTimerContextField, CtField.Initializer.byExpr(format("new %s(\"jms.destination.total\");", TimerContext.class.getName())));
    }

    private static ClassPool classPool() {
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        return pool;
    }
}