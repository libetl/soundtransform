package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public interface FluentClientWithFreqs extends FluentClientCommon {

    FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, InputStreamInfo isi) throws SoundTransformException;

    FluentClientWithFreqs withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientWithFreqs withAPack (String packName, String jsonContent) throws SoundTransformException;
}
