package org.toilelibre.libe.soundtransform.actions.fluent;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithParallelizedClients extends FluentClientCommon {

    FluentClientSoundImported mixAllInOneSound () throws SoundTransformException;
    
    <T> List<T> stopWithResults (Class<T> resultClass);
}
