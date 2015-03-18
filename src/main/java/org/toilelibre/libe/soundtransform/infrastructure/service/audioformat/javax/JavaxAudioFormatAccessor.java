package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import org.toilelibre.libe.soundtransform.ioc.AllAgnosticAccessorsAndBindings;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;

public abstract class JavaxAudioFormatAccessor extends AllAgnosticAccessorsAndBindings {

    @Override
    protected AudioFileHelper provideAudioFileHelper () {
        return new JavazoomAudioFileHelper ();
    }

    @Override
    protected AudioFormatParser provideAudioFormatParser () {
        return new WavAudioFormatParser ();
    }

}
