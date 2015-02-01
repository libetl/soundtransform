package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithFile extends FluentClientCommon {

    FluentClientSoundImported convertIntoSound () throws SoundTransformException;

    FluentClientWithInputStream importToStream () throws SoundTransformException;

    FluentClientWithFile playIt () throws SoundTransformException;

    File stopWithFile ();

    FluentClientWithFile withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientWithFile withAPack (String packName, String jsonContent) throws SoundTransformException;
}
