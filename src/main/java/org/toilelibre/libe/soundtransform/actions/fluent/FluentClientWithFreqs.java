package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithFreqs {
    FluentClientSoundImported shapeIntoSound (String packName, String instrumentName) throws SoundTransformException;

    FluentClientReady andAfterStart () throws SoundTransformException;
}
