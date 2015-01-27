package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class SoundGenerateTest extends SoundTransformTest {

    @Test
    public void generateA440HzSound () throws SoundTransformException {
        final int length = 4000;
        final int soundfreq = 440;
        final int sampleInBytes = 2;

        final int samplerate = 44100;
        final long [] signal = new long [length];
        for (int j = 0 ; j < length ; j++) {
            signal [j] = (long) (Math.sin ((j * soundfreq * 2 * Math.PI) / samplerate) * 32768.0);
        }
        final Sound s = new Sound (signal, sampleInBytes, samplerate, 1);

        final InputStream ais = $.create (TransformSoundService.class).toStream (new Sound [] { s }, new InputStreamInfo (1, s.getSamples ().length, sampleInBytes * 8, samplerate, false, true));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (ConvertAudioFileService.class).writeInputStream (ais, fDest);
    }

    @Test
    public void seeHps () throws SoundTransformException {
        final int length = 10000;
        final int soundfreq = 440;
        final int sampleInBytes = 2;

        final int samplerate = 44100;
        final long [] signal = new long [length];
        for (int j = 0 ; j < length ; j++) {
            signal [j] = (long) (Math.sin ((j * soundfreq * 2 * Math.PI) / samplerate) * 32768.0);
        }
        final Sound s = new Sound (signal, sampleInBytes, samplerate, 1);
        final SoundTransformation st = new SimpleFrequencySoundTransformation<Complex []> ();
        st.transform (s);

    }
}
