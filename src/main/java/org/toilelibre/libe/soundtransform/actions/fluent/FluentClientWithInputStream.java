package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithInputStream {
    FluentClientSoundImported importToSound () throws SoundTransformException;

    FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException;

    FluentClientWithFile writeToFile (File file) throws SoundTransformException;

    FluentClientReady andAfterGo () throws SoundTransformException;
}
