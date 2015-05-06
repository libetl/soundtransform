package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class SoundToStringTest extends SoundTransformTest {

    @Test
    public void testFsToString () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("before.wav").getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);
        final Channel s = $.select (InputStreamToSoundService.class).fromInputStream (ais).getChannels () [0];
        new SimpleFrequencySoundTransform<Complex []> () {

            @Override
            public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs) {
                new Slf4jObserver (LogLevel.WARN).notify (fs.toString ());
                return super.transformFrequencies (fs);
            }

        }.transform (s);

    }

    @Test
    public void testToString () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("before.wav").getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);
        new Slf4jObserver (LogLevel.WARN).notify ($.select (InputStreamToSoundService.class).fromInputStream (ais).getChannels () [0].toString ());

    }
}
