package org.toilelibre.libe.soundtransform.observer;

public interface LogAware {

	public void setObservers (TransformObserver [] observers);

	public void log (LogEvent event);
}
