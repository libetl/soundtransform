package org.toilelibre.libe.soundtransform.model.observer;

import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface EventCode {

    LogLevel getLevel();

    String getMessageFormat();

    String name();

}
