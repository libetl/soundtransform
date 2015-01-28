package org.toilelibre.libe.soundtransform.ioc.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AndroidAudioFileHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.NoOpFormatParser;
import org.toilelibre.libe.soundtransform.infrastructure.service.play.android.AndroidPlaySoundProcessor;
import org.toilelibre.libe.soundtransform.ioc.ImplAgnosticRootModule;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class AndroidRootModule extends ImplAgnosticRootModule {

    @Override
    protected AudioFileHelper provideAudioFileHelper () {
        return new AndroidAudioFileHelper ();
    }

    @Override
    protected AudioFormatParser provideAudioFormatParser () {
        return new NoOpFormatParser ();
    }

    @SuppressWarnings ("unchecked")
    @Override
    protected PlaySoundProcessor<?> providePlaySoundProcessor () {
        return new AndroidPlaySoundProcessor ();
    }

}
