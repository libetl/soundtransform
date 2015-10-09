package org.toilelibre.libe.soundtransform.model.logging;

public interface Observer extends Cloneable {

    void notify (LogEvent logEvent);
}
