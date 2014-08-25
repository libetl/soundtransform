package org.toilelibre.soundtransform.observer;

public interface LogAware {

	public void setObservers (TransformObserver [] observers);

	public void log (LogEvent event);
}
