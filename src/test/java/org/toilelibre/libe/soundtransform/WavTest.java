package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.EqualizerSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.GaussianEqualizerSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LevelSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LinearRegressionSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PralongAndCarlileSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PurifySoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.ReduceNoiseSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.FadeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.InsertPartSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MaximumLikelihoodSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NormalizeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReverseSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class WavTest extends SoundTransformTest {

    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        shortInput  = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());

    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void test8bits () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new EightBitsSoundTransform (25)).exportToFile (this.output);
    }

    @Test
    public void peakFindTestWithNotDefaultWindowLength () throws SoundTransformException {
        final File file = new File (this.classLoader.getResource ("piano1c.wav").getFile ());
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().applyAndStop (new HarmonicProductSpectrumSoundTransform<Serializable> (true, 100, 1024, false, 0.2f));
    }

    @Test
    public void testShortSoundCepstrum () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().extractSubSound (0, 4000).applyAndStop (new CepstrumSoundTransform<Serializable> (100, true));
    }

    @Test
    public void testLongSoundCepstrum () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().extractSubSound (0, 4000).applyAndStop (new CepstrumSoundTransform<Serializable> (100, false));
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testFadeAboveSoundLength () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransform (Integer.MAX_VALUE, true)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testFadeBelowZero () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransform (-5, true)).exportToFile (this.output);
    }

    @Test
    public void testFadeIn () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransform (100000, true)).exportToFile (this.output);
    }

    @Test
    public void testFadeOut () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new FadeSoundTransform (100000, false)).exportToFile (this.output);
    }

    @Test
    public void testFreqNoOp () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new SimpleFrequencySoundTransform<Serializable> ()).exportToFile (this.output);
    }

    @Test
    public void testGaussianEqualizer () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new GaussianEqualizerSoundTransform ()).exportToFile (this.output);
    }

    @Test
    public void testInsert () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("gpiano4.wav").getFile ());
        final Sound sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSound ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransform (sound2, 1000)).exportToFile (this.output);
    }

    @Test
    public void testInsertAfterEnd () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("gpiano4.wav").getFile ());
        final Sound sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSound ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransform (sound2, 100000)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testInsertWrongFormat () throws SoundTransformException {
        final File input2 = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final Sound sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSound ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new InsertPartSoundTransform (sound2, -100000)).exportToFile (this.output);
    }

    @Test
    public void testLinearReg () throws SoundTransformException {
        // will remove the high freqs and smooth the signal
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new LinearRegressionSoundTransform (25)).exportToFile (this.output);
    }

    @Test
    public void testMix () throws SoundTransformException {
        final File input1 = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());
        final File input2 = new File (this.classLoader.getResource ("piano3e.wav").getFile ());
        final Sound sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input2).convertIntoSound ().stopWithSound ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (input1).convertIntoSound ().apply (new MixSoundTransform (sound2)).exportToFile (this.output);
    }

    @Test
    public void testNoOp () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NoOpSoundTransform ()).exportToFile (this.output);
    }

    @Test
    public void testNormalize () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NormalizeSoundTransform (1.0f)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeAbove1 () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new NormalizeSoundTransform (-0.5f)).exportToFile (this.output);
    }

    @Test (expected = SoundTransformException.class)
    public void testNormalizeBelow0 () throws SoundTransformException {
        new NormalizeSoundTransform (-0.5f);
    }

    @Test
    public void testPitch () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new PitchSoundTransform (100)).exportToFile (this.output);
    }

    @Test
    public void testPurify () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new PurifySoundTransform ()).exportToFile (this.output);
    }

    @Test
    public void testReduceNoise () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new ReduceNoiseSoundTransform (30)).exportToFile (this.output);
    }

    @Test
    public void testRemoveLowFreqs () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ()
        .apply (new EqualizerSoundTransform (new double [] { 0, 100, 200, 300, 400, 500, 600, 1000, 2000, 3000, 4000, 5000, 8000, 15000, 20000 }, new double [] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 })).exportToFile (this.output);
    }

    @Test
    public void testReverse () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().apply (new ReverseSoundTransform ()).exportToFile (this.output);
    }

    @Test
    public void testShape () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().findLoudestFrequencies (new HarmonicProductSpectrumSoundTransform<Serializable> (100, true, 0.1f)).shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 44100))
        .exportToFile (this.output);

    }

    @Test
    @Ignore
    public void testShapeCepstrum () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.VERBOSE)).withFile (this.input).convertIntoSound ().apply (new LevelSoundTransform (300)).findLoudestFrequencies (new CepstrumSoundTransform<Serializable> (100, false))
        .shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 44100)).exportToFile (this.output);

    }

    @Test
    @Ignore
    public void compareLoudestFreqsArray () throws SoundTransformException {
        // WARN : quite long
        final Sound sound = FluentClient.start ().withFile (this.shortInput).convertIntoSound ().apply (new LevelSoundTransform (100)).apply (new NormalizeSoundTransform (1)).stopWithSound ();
        final String s1 = Arrays.toString (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.VERBOSE)).withSound (sound).findLoudestFrequencies (new HarmonicProductSpectrumSoundTransform<Serializable> (true, false)).stopWithFreqs ().get (0))
                .replaceAll ("(\\[| )([0-9])\\.([0-9])(\\]|,)", "$100$2.$300$4").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9]),", " $1$2$3.$400,").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9]),", " $1$2$3.$4$50,")
                .replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9])([0-9])([0-9]*),", " $1$2$3.$4$5$6,");
        final String s2 = Arrays.toString (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.VERBOSE)).withSound (sound).findLoudestFrequencies (new CepstrumSoundTransform<Serializable> (1, true)).stopWithFreqs ().get (0))
                .replaceAll ("(\\[| )([0-9])\\.([0-9])(\\]|,)", "$100$2.$300$4").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9]),", " $1$2$3.$400,").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9]),", " $1$2$3.$4$50,")
                .replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9])([0-9])([0-9]*),", " $1$2$3.$4$5$6,");
        final String s3 = Arrays
                .toString (
                        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.VERBOSE)).withSound (sound).apply (new PralongAndCarlileSoundTransform ()).findLoudestFrequencies (new MaximumLikelihoodSoundTransform (sound.getSamplesLength (), sound.getSamplesLength () + 1, 0, 500))
                        .stopWithFreqs ().get (0)).replaceAll ("(\\[| )([0-9])\\.([0-9])(\\]|,)", "$100$2.$300$4").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9]),", " $1$2$3.$400,").replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9]),", " $1$2$3.$4$50,")
                        .replaceAll (" ([0-9])([0-9])([0-9])\\.([0-9])([0-9])([0-9])([0-9]*),", " $1$2$3.$4$5$6,");
        new Slf4jObserver (LogLevel.INFO).notify (s1);
        new Slf4jObserver (LogLevel.INFO).notify (s2);
        new Slf4jObserver (LogLevel.INFO).notify (s3);
    }

    @Test
    public void testSlowdown () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.shortInput).convertIntoSound ().apply (new SlowdownSoundTransform (200, 1.2f, 512)).exportToFile (this.output);
    }

    @Test
    public void testSpeedUp () throws SoundTransformException {
        // WARN : quite long
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (this.input).convertIntoSound ().extractSubSound (0, 100000).apply (new SpeedUpSoundTransform<Serializable> (200, 1.5f)).exportToFile (this.output);
    }
}
