package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatAccessor;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConverterLauncher;

public abstract class AudioFormatConverterAccessor extends AudioFormatAccessor {

    protected ConverterLauncher<Converter> provideConverterLauncher () {
        return new ProxyConverterLauncher ();
    }
}
