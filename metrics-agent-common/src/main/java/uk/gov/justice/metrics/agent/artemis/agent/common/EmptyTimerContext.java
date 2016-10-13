package uk.gov.justice.metrics.agent.artemis.agent.common;


/**
 * This timer context does not measure anything
 */
public class EmptyTimerContext implements TimerContext {
    @Override
    public void startTimer(final Object timerId) {

    }

    @Override
    public void stopTimer(final Object timerId) {

    }
}
