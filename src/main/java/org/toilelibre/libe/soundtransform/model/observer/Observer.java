package org.toilelibre.libe.soundtransform.model.observer;

public interface Observer extends Cloneable {

    public void notify (LogEvent logEvent);
}
