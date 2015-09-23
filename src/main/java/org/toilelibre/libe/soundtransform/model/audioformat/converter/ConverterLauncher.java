package org.toilelibre.libe.soundtransform.model.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public interface ConverterLauncher<TConverter> {

    Entry<StreamInfo, ByteArrayOutputStream> convert (TConverter converter, InputStream inputStream) throws SoundTransformException;
}
