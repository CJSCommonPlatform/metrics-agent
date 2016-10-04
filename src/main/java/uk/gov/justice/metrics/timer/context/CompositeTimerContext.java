package uk.gov.justice.metrics.timer.context;

public class CompositeTimerContext implements TimerContext {

    private TimerContext[] timerContexts;

    public CompositeTimerContext(final TimerContext... timerContexts) {
        this.timerContexts = timerContexts;
    }

    @Override
    public void startTimer(final String timerId) {
        for (TimerContext timerContext : timerContexts) {
            timerContext.startTimer(timerId);
        }

    }

    @Override
    public void stopTimer(final String timerId) {
        for (TimerContext timerContext : timerContexts) {
            timerContext.stopTimer(timerId);
        }

    }
}
