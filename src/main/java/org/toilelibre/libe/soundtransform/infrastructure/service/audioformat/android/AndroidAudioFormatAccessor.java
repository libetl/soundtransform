package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import org.toilelibre.libe.soundtransform.ioc.AllAgnosticAccessorsAndBindings;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;

public abstract class AndroidAudioFormatAccessor extends AllAgnosticAccessorsAndBindings {

    protected AudioFileHelper provideAudioFileHelper() {
        return new AndroidAudioFileHelper();
    }

    protected AudioFormatParser provideAudioFormatParser() {
        return new NoOpFormatParser();
    }

}
