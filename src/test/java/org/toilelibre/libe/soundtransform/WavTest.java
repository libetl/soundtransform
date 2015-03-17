package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.GaussianEqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PurifySoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.FadeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.InsertPartSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NormalizeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class WavTest extends SoundTransformTest {

    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        shortInput  = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());

    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void test8bits () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new EightBitsSoundTransformation (25));

    }

    @Test
    public void testCepstrum () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, new CepstrumSoundTransformation<Serializable> (100));
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testFadeAboveSoundLength () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new FadeSoundTransformation (Integer.MAX_VALUE, true));
    }

    @Test (expected = SoundTransformException.class)
    public void testFadeBelowZero () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new FadeSoundTransformation (-5, true));
    }

    @Test
    public void testFadeIn () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new FadeSoundTransformation (100000, true));
    }

    @Test
    public void testFadeOut () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new FadeSoundTransformation (100000, false));
    }

    @Test
    public void testFreqNoOp () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, $.create (SimpleFrequencySoundTransformation.class));
    }

    @Test
    public void testGaussianEqualizer () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new GaussianEqualizerSoundTransformation ());
    }

    @Test
    public void testInsert () throws SoundTransformException {
        final InputStream ais = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromFile (new File (this.classLoader.getResource ("gpiano4.wav").getFile ()));
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, new InsertPartSoundTransformation ($.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream (ais), 1000));
    }

    @Test
    public void testInsertAfterEnd () throws SoundTransformException {
        final InputStream ais = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromFile (new File (this.classLoader.getResource ("gpiano4.wav").getFile ()));
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, new InsertPartSoundTransformation ($.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream (ais), 15000));
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testInsertWrongFormat () throws SoundTransformException {
        final InputStream ais = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromFile (new File (this.classLoader.getResource ("gpiano4.wav").getFile ()));
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new InsertPartSoundTransformation ($.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream (ais), 1000));
    }

    @Test
    public void testLinearReg () throws SoundTransformException {
        // will remove the high freqs and smooth the signal
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new LinearRegressionSoundTransformation (25));
    }

    @Test
    public void testMix () throws SoundTransformException {
        final File input1 = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());
        final File input2 = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final TransformSoundService transform = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN));
        final Sound [] sound2 = transform.fromInputStream (transform.fromFile (input2));
        transform.transformFile (input1, this.output, new MixSoundTransformation (Arrays.<Sound []> asList (sound2)));
    }

    @Test
    public void testNoOp () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new NoOpSoundTransformation ());

    }

    @Test
    public void testNormalize () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new NormalizeSoundTransformation (1.0f));
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeAbove1 () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new NormalizeSoundTransformation (-0.5f));
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeBelow0 () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new NormalizeSoundTransformation (-0.5f));
    }

    @Test
    public void testPitch () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new PitchSoundTransformation (100));

    }

    @Test
    public void testPurify () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, $.create (PurifySoundTransformation.class));

    }

    @Test
    public void testRemoveLowFreqs () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output,
                $.create (EqualizerSoundTransformation.class, new double [] { 0, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 16000, 18000, 24000 }, new double [] { 0, 0, 0.1, 0.3, 0.7, 1, 1, 1, 1, 1, 1 }));
    }

    @Test
    public void testReverse () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, new ReverseSoundTransformation ());
    }

    @Test
    public void testShape () throws SoundTransformException {
        // WARN : quite long
        new Slf4jObserver (LogLevel.WARN).notify ("Loading default pack");
        final Library library = $.select (Library.class);
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack (library, "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, new ShapeSoundTransformation ("default", "simple_piano"));

    }

    @Test
    public void testSlowdown () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.input, this.output, $.create (SlowdownSoundTransformation.class, 200, 1.2f, 512));

    }

    @Test
    public void testSpeedUp () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.shortInput, this.output, $.create (SpeedUpSoundTransformation.class, 200, 1.5f));

    }
}
