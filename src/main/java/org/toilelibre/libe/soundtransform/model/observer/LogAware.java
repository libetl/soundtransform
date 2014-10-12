package org.toilelibre.libe.soundtransform.model.observer;

public interface LogAware {

	public void setObservers (Observer [] observers);

	public void log (LogEvent event);
}
