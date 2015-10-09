package org.toilelibre.libe.soundtransform.model.logging;

public interface LogAware<T> {

    void log (LogEvent event);

    T setObservers (Observer... observers);
}
