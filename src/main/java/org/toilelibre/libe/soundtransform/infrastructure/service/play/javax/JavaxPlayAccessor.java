package org.toilelibre.libe.soundtransform.infrastructure.service.play.javax;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.JavaxAudioFormatAccessor;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public abstract class JavaxPlayAccessor extends JavaxAudioFormatAccessor {

    @Override
    protected PlaySoundProcessor providePlaySoundProcessor () {
        return new LineListenerPlaySoundProcessor ();
    }
}
