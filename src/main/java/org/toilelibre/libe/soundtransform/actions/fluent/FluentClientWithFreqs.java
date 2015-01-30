package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public interface FluentClientWithFreqs {
    FluentClientReady andAfterStart () throws SoundTransformException;

    FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, InputStreamInfo isi) throws SoundTransformException;
}
