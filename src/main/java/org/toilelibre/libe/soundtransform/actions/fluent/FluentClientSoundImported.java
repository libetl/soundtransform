package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientSoundImported extends FluentClientCommon {

    FluentClientSoundImported apply (SoundTransformation st) throws SoundTransformException;

    FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException;

    FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    FluentClientWithFile exportToFile (File file1) throws SoundTransformException;

    FluentClientWithInputStream exportToStream () throws SoundTransformException;

    FluentClientSoundImported playIt () throws SoundTransformException;

    FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException;

    Sound [] stopWithSounds ();

    FluentClientSoundImported withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientSoundImported withAPack (String packName, String jsonContent) throws SoundTransformException;
}