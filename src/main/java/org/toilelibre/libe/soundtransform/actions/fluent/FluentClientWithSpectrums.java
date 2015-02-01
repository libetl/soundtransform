package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithSpectrums extends FluentClientCommon {

    FluentClientSoundImported extractSound () throws SoundTransformException;

    FluentClientWithSpectrums playIt () throws SoundTransformException;

    Spectrum<?> [] stopWithSpectrums ();

    FluentClientWithSpectrums withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientWithSpectrums withAPack (String packName, String jsonContent) throws SoundTransformException;
}
