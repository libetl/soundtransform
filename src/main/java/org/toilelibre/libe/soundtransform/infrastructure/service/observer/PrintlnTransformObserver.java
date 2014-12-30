package org.toilelibre.libe.soundtransform.infrastructure.service.observer;

import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class PrintlnTransformObserver implements Observer {

    private boolean    paranoiac;

    public PrintlnTransformObserver () {

    }

    public PrintlnTransformObserver (final boolean withParanoiac) {
        this.paranoiac = withParanoiac;
    }

    @Override
    public void notify (final LogEvent logEvent) {
        if (logEvent.getLevel () != LogLevel.PARANOIAC || this.paranoiac == true) {
            System.out.println (logEvent);
        }
    }

}
