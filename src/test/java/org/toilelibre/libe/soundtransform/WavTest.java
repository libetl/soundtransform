package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.GaussianEqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PurifySoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.FadeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.InsertPartSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NormalizeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransformation;
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
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new EightBitsSoundTransformation (25)).exportToFile (this.output);
    }

    @Test
    public void peakFindTest () throws SoundTransformException {
        final PeakFindSoundTransformation<Serializable> cepstrum = new CepstrumSoundTransformation<Serializable> (100, false, true);
        File file = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final PeakFindSoundTransformation<Serializable> hps = new PeakFindWithHPSSoundTransformation<Serializable> (true);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().apply (cepstrum).exportToFile (this.output).importToStream ().stopWithStreamInfo ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().apply (hps).exportToFile (this.output).importToStream ().stopWithStreamInfo ();
        new Slf4jObserver (LogLevel.INFO).notify ("Peak find with the file " + file.getPath() + " : cepstrum -> " + cepstrum.getLoudestFreqs () [0] + ", hps -> " + hps.getLoudestFreqs() [0]);
    }

    @Test
    public void testShortSoundCepstrum () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().extractSubSound (0, 4000).apply (new CepstrumSoundTransformation<Serializable> (100, false, true)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testFadeAboveSoundLength () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransformation (Integer.MAX_VALUE, true)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testFadeBelowZero () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransformation (-5, true)).exportToFile (this.output);
    }

    @Test
    public void testFadeIn () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransformation (100000, true)).exportToFile (this.output);
    }

    @Test
    public void testFadeOut () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransformation (100000, false)).exportToFile (this.output);
    }

    @Test
    public void testFreqNoOp () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new SimpleFrequencySoundTransformation<Serializable> ()).exportToFile (this.output);
    }

    @Test
    public void testGaussianEqualizer () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new GaussianEqualizerSoundTransformation ()).exportToFile (this.output);
    }

    @Test
    public void testInsert () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("gpiano4.wav").getFile ());
        final Sound [] sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransformation (sound2, 1000)).exportToFile (this.output);
    }

    @Test
    public void testInsertAfterEnd () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("gpiano4.wav").getFile ());
        final Sound [] sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransformation (sound2, 100000)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testInsertWrongFormat () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final Sound [] sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransformation (sound2, -100000)).exportToFile (this.output);
    }

    @Test
    public void testLinearReg () throws SoundTransformException {
        // will remove the high freqs and smooth the signal
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new LinearRegressionSoundTransformation (25)).exportToFile (this.output);
    }

    @Test
    public void testMix () throws SoundTransformException {
        final File input1 = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());
        final File input2 = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final Sound [] sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input1).convertIntoSound ().apply (new MixSoundTransformation (Arrays.<Sound []> asList (sound2))).exportToFile (this.output);
    }

    @Test
    public void testNoOp () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NoOpSoundTransformation ()).exportToFile (this.output);
    }

    @Test
    public void testNormalize () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NormalizeSoundTransformation (1.0f)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeAbove1 () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NormalizeSoundTransformation (-0.5f)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeBelow0 () throws SoundTransformException {
        new NormalizeSoundTransformation (-0.5f);
    }

    @Test
    public void testPitch () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new PitchSoundTransformation (100)).exportToFile (this.output);
    }

    @Test
    public void testPurify () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new PurifySoundTransformation ()).exportToFile (this.output);
    }

    @Test
    public void testRemoveLowFreqs () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ()
        .apply (new EqualizerSoundTransformation (new double [] { 0, 100, 200, 300, 400, 500, 600, 1000, 2000, 3000, 4000, 5000, 8000, 15000, 20000 }, new double [] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 })).exportToFile (this.output);
    }

    @Test
    public void testReverse () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new ReverseSoundTransformation ()).exportToFile (this.output);
    }

    @Test
    public void testShape () throws SoundTransformException {
        // WARN : quite long
        final Library library = $.select (Library.class);
        ((ImportPackService<?>) $.select (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN))).importPack (library, "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new ShapeSoundTransformation ("default", "simple_piano")).exportToFile (this.output);

    }

    @Test
    public void testSlowdown () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new SlowdownSoundTransformation (200, 1.2f, 512)).exportToFile (this.output);
    }

    @Test
    public void testSpeedUp () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().extractSubSound (0, 100000).apply (new SpeedUpSoundTransformation<Serializable> (200, 1.5f)).exportToFile (this.output);
    }
}
