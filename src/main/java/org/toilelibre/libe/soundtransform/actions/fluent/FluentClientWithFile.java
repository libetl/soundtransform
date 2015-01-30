package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithFile {
    FluentClientSoundImported convertIntoSound () throws SoundTransformException;

    FluentClientWithInputStream importToStream () throws SoundTransformException;

    FluentClientReady andAfterGo () throws SoundTransformException;

    FluentClientWithFile playIt () throws SoundTransformException;
}
