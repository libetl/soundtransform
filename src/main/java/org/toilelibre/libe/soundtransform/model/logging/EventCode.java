package org.toilelibre.libe.soundtransform.model.logging;

import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

public interface EventCode {

    LogLevel getLevel ();

    String getMessageFormat ();

    String name ();

}
