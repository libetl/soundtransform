package org.toilelibre.libe.soundtransform.model.observer;

public interface LogAware {

    public void log (LogEvent event);

    public void setObservers (Observer [] observers);
}
