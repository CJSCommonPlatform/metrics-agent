package uk.gov.justice.metrics.timer.context;


/**
 * This timer context does not measure anything
 */
public class EmptyTimerContext implements TimerContext {
    @Override
    public void startTimer(final String timerId) {

    }

    @Override
    public void stopTimer(final String timerId) {

    }
}
