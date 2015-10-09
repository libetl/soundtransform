package org.toilelibre.libe.soundtransform.infrastructure.service.play.javax;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.JavaxAudioFormatAccessor;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

public abstract class JavaxPlayAccessor extends JavaxAudioFormatAccessor {

    @Override
    protected PlayObjectProcessor providePlaySoundProcessor () {
        return new LineListenerPlayObjectProcessor ();
    }
}
