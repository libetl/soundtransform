package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.audioformat.converter.ConverterLauncher;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

class ProxyConverterLauncher extends AbstractLogAware<ProxyConverterLauncher> implements ConverterLauncher<Converter> {

    @Override
    public Entry<StreamInfo, ByteArrayOutputStream> convert (Converter converter, InputStream inputStream) throws SoundTransformException {

        return converter.convert (inputStream);
    }

}
