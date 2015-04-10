package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithParallelizedClients extends FluentClientCommon {

    FluentClientSoundImported mixAllInOneSound () throws SoundTransformException;

    <T> T [] stopWithResults (Class<T> resultClass);
}
