package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import org.toilelibre.libe.soundtransform.ioc.AllAgnosticAccessorsAndBindings;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;

public abstract class JavaxAudioFormatAccessor extends AllAgnosticAccessorsAndBindings {

    protected AudioFileHelper provideAudioFileHelper() {
        return new JavazoomAudioFileHelper();
    }

    protected AudioFormatParser provideAudioFormatParser() {
        return new WavAudioFormatParser();
    }

}
