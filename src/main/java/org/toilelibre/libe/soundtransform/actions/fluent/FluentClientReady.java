package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public interface FluentClientReady {

    FluentClientReady withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    FluentClientReady withAPack (String packName, String jsonContent) throws SoundTransformException;

    FluentClientWithInputStream withAudioInputStream (InputStream is);

    FluentClientWithFile withClasspathResource (String resource) throws SoundTransformException;

    FluentClientWithFile withFile (File file);

    FluentClientWithFreqs withFreqs (int [] freqs);

    FluentClientWithInputStream withRawInputStream (InputStream is, InputStreamInfo isInfo) throws SoundTransformException;

    FluentClientSoundImported withSounds (Sound [] sounds);

    FluentClientWithSpectrums withSpectrums (List<Spectrum<?> []> spectrums);
}
