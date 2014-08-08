package org.toilelibre.soundtransform.observer;


public class PrintlnTransformObserver implements TransformObserver {

	@Override
	public void notify (LogEvent logEvent) {
		System.out.println (logEvent);
	}

}
