package org.toilelibre.soundtransform.observer;

import org.toilelibre.soundtransform.observer.LogEvent.LogLevel;

public class PrintlnTransformObserver implements TransformObserver {

	private boolean paranoiac;

	public PrintlnTransformObserver() {

	}

	public PrintlnTransformObserver(boolean withParanoiac) {
		this.paranoiac = withParanoiac;
	}

	@Override
	public void notify(LogEvent logEvent) {
		if (logEvent.getLevel() != LogLevel.PARANOIAC || this.paranoiac == true) {
			System.out.println(logEvent);
		}
	}

}
