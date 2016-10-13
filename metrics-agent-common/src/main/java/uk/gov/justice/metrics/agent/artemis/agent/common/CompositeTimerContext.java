package uk.gov.justice.metrics.agent.artemis.agent.common;

public class CompositeTimerContext implements TimerContext {

    private TimerContext[] timerContexts;

    public CompositeTimerContext(final TimerContext... timerContexts) {
        this.timerContexts = timerContexts;
    }

    @Override
    public void startTimer(final Object timerId) {
        for (TimerContext timerContext : timerContexts) {
            timerContext.startTimer(timerId);
        }

    }

    @Override
    public void stopTimer(final Object timerId) {
        for (TimerContext timerContext : timerContexts) {
            timerContext.stopTimer(timerId);
        }

    }
}
