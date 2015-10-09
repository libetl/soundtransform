package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AndroidAudioFormatAccessor;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

public abstract class AndroidPlayAccessor extends AndroidAudioFormatAccessor {

    @Override
    protected PlayObjectProcessor providePlaySoundProcessor () {
        return new AndroidPlayObjectProcessor ();
    }
}
