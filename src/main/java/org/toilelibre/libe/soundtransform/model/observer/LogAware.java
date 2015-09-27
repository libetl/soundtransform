package org.toilelibre.libe.soundtransform.model.observer;

public interface LogAware<T> {

    void log (LogEvent event);

    T setObservers (Observer... observers);
}
