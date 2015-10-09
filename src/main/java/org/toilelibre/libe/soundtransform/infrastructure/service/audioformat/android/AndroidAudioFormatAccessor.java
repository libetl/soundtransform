package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import org.toilelibre.libe.soundtransform.ioc.AllAgnosticAccessorsAndBindings;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConvertProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatParser;

public abstract class AndroidAudioFormatAccessor extends AllAgnosticAccessorsAndBindings {

    @Override
    protected AudioFileHelper provideAudioFileHelper () {
        return new AndroidAudioFileHelper ();
    }

    @Override
    protected AudioFormatParser provideAudioFormatParser () {
        return new NoOpFormatParser ();
    }

    @Override
    protected ConvertProcessor provideConvertProcessor () {
        return new AndroidConvertProcessor ();
    }
}
