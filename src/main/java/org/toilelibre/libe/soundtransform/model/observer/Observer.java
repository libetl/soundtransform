package org.toilelibre.libe.soundtransform.model.observer;

public interface Observer extends Cloneable {

    void notify (LogEvent logEvent);
}
