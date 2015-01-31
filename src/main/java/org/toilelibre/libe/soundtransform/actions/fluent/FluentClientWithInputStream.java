package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithInputStream {
    FluentClientReady andAfterStart () throws SoundTransformException;

    FluentClientSoundImported importToSound () throws SoundTransformException;

    FluentClientWithInputStream playIt () throws SoundTransformException;

    FluentClientWithInputStream withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientWithInputStream withAPack (String packName, String jsonContent) throws SoundTransformException;

    FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException;

    FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    FluentClientWithFile writeToFile (File file) throws SoundTransformException;

}
