package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.NormalizeSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PurifySoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SpeedUpSoundTransformation;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;

public class WavTest {

    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    // private File input = new File
    // ("D:/Mes Soirées 80's-Spécial Discothèques/CD 1/08 Captain Sensible-Wot.mp3");
    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void test8bits () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new EightBitsSoundTransformation (25));

    }

    @Test
    public void testFreqNoOp () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, $.create (SimpleFrequencySoundTransformation.class));

    }

    @Test
    public void testLinearReg () throws SoundTransformException {
        // will remove the high freqs and smooth the signal
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new LinearRegressionSoundTransformation (25));

    }

    @Test
    public void testNoOp () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new NoOpSoundTransformation ());

    }

    @Test
    public void testNormalize () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new NormalizeSoundTransformation ());

    }

    @Test
    public void testPitch () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new PitchSoundTransformation (100));

    }

    // @Test
    public void testPurify () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, $.create (PurifySoundTransformation.class));

    }

    @Test
    public void testRemoveLowFreqs () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, $.create (EqualizerSoundTransformation.class, new double [] { 0, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 16000, 18000, 24000 }, new double [] { 0, 0, 0.1, 0.3, 0.7, 1, 1, 1, 1, 1, 1 }));

    }

    @Test
    public void testReverse () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new ReverseSoundTransformation ());

    }

    @Test
    public void testShape () throws SoundTransformException, FileNotFoundException {
        // WARN : quite long
        new Slf4jObserver ().notify ("Loading default pack");
        final Library library = $.select (Library.class);
        $.create (ImportPackService.class).setObservers (new Slf4jObserver ()).importPack (library, "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultPack.json"));
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, new ShapeSoundTransformation (library.getPack ("default"), "simple_piano"));

    }

    // @Test
    public void testSlowdown () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, $.create (SlowdownSoundTransformation.class, 200, 1.2f));

    }

    // @Test
    public void testSpeedUp () throws SoundTransformException {
        // WARN : quite long
        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (this.input, this.output, $.create (SpeedUpSoundTransformation.class, 200, 1.5f));

    }
}
