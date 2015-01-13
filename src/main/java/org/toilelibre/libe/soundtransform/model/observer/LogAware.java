package org.toilelibre.libe.soundtransform.model.observer;

public interface LogAware<T> {

    public void log (LogEvent event);

    public T setObservers (Observer... observers);
}
