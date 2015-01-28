package org.toilelibre.libe.soundtransform.ioc.test;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.JavazoomAudioFileHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.WavAudioFormatParser;
import org.toilelibre.libe.soundtransform.infrastructure.service.play.javax.LineListenerPlaySoundProcessor;
import org.toilelibre.libe.soundtransform.ioc.ImplAgnosticRootModule;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class JavaXRootModule extends ImplAgnosticRootModule {

    @Override
    protected AudioFileHelper provideAudioFileHelper () {
        return new JavazoomAudioFileHelper ();
    }

    @Override
    protected AudioFormatParser provideAudioFormatParser () {
        return new WavAudioFormatParser ();
    }

    @SuppressWarnings ("unchecked")
    @Override
    protected PlaySoundProcessor<?> providePlaySoundProcessor () {
        return new LineListenerPlaySoundProcessor ();
    }

}
