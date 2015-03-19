package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import org.toilelibre.libe.soundtransform.infrastructure.service.appender.AppenderAccessor;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToByteArrayHelper;

public abstract class AudioFormatAccessor extends AppenderAccessor {

    protected InputStreamToByteArrayHelper provideInputStreamToByteArrayHelper () {
        return new WriteInputStreamToByteArray ();
    }

}
