package uk.gov.justice.metrics.timer.context;

/**
 * Created by jacek on 03/10/16.
 */
public interface TimerContext {
    void startTimer(String timerId);

    void stopTimer(String timerId);
}
