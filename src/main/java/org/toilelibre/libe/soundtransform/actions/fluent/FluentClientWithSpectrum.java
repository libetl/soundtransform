package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithSpectrum extends FluentClientCommon {

    FluentClientSoundImported extractSound () throws SoundTransformException;

    FluentClientWithSpectrum playIt () throws SoundTransformException;

    Spectrum<?> stopWithSpectrum ();

    FluentClientWithSpectrum withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientWithSpectrum withAPack (String packName, String jsonContent) throws SoundTransformException;
}
