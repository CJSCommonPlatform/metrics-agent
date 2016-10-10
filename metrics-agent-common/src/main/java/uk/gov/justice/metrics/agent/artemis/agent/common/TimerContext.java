package uk.gov.justice.metrics.agent.artemis.agent.common;

public interface TimerContext {
    void startTimer(Object timerId);

    void stopTimer(Object timerId);
}
