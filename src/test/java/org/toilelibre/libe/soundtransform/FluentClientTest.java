package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class FluentClientTest extends SoundTransformTest {

    @Test
    public void appendTest () throws SoundTransformException {
        final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano4.wav").convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").convertIntoSound ().append (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void backAndForth () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().apply (new NoOpSoundTransformation ()).exportToClasspathResource ("before.wav").convertIntoSound ();
    }

    @Test
    public void changeFormat () throws SoundTransformException {
        final InputStreamInfo isi = new InputStreamInfo (1, 0, 1, 8000, false, true);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").convertIntoSound ().changeFormat (isi);
    }

    @Test
    public void cutsound () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().cutSubSound (100000, 600000).exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformException.class)
    public void cutsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test
    public void findLoudestFreqs () throws SoundTransformException {
        org.junit.Assert.assertNotNull (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").convertIntoSound ().findLoudestFrequencies ().stopWithFreqs ());
    }

    @Test
    public void getDefaultPack () throws SoundTransformException {
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultPack.json");
        final Pack pack = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).stopWithAPack ("default");
        Assert.assertNotNull (pack);
    }

    @Test
    public void loop () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").convertIntoSound ().loop (100000).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void mixTest () throws SoundTransformException {
        final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/Piano3-E.wav").convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").convertIntoSound ().mixWith (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void playIt () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").playIt ().convertIntoSound ().playIt ().exportToStream ().playIt ();
        } catch (final PlaySoundException pse) {
            new Slf4jObserver ().notify ("This build environment cannot play a sound (ignoring) " + pse);
        }
    }

    @Test
    public void readFormat () throws SoundTransformException {
        final InputStreamInfo isInfo = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("notes/g-piano3.wav").importToStream ().stopWithInputStreamInfo ();
        isInfo.hashCode ();
    }

    @Test
    public void readInputStreamInfo () throws SoundTransformException {
        final InputStreamInfo isi = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").importToStream ().stopWithInputStreamInfo ();
        Assert.assertNotNull (isi);
        new Slf4jObserver ().notify (isi.toString ());
    }

    @Test
    public void readRawInputStream () throws SoundTransformException {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [65536];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        final InputStream is = new ByteArrayInputStream (data);
        final InputStreamInfo isi = new InputStreamInfo (1, 32768, 2, 8000, false, true);

        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withRawInputStream (is, isi).importToSound ().exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void replacePart1 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 15, 16, 17, 18 }, FluentClient.start ().withFreqs (array1).replacePart (array2, 4).stopWithFreqs (), 0);
    }

    @Test
    public void replacePart2 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 5, 6, 7, 15, 16, 17, 18 }, FluentClient.start ().withFreqs (array1).replacePart (array2, 7).stopWithFreqs (), 0);
    }

    @Test
    public void replacePart3 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 15, 16, 17, 18 }, FluentClient.start ().withFreqs (array1).replacePart (array2, 11).stopWithFreqs (), 0);
    }

    // Exactly the same code run as WavTest.testShape
    // @Test
    public void shapeASoundTest () throws SoundTransformException {
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultPack.json");
        final InputStreamInfo isi = new InputStreamInfo (1, 770164, 2, 48000, false, true);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).withClasspathResource ("before.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("default", "simple_piano", isi);
    }

    @Test
    public void simpleLifeCycle () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().apply (new EightBitsSoundTransformation (25)).exportToClasspathResource ("after.wav");
    }

    @Test
    public void spectrumTest () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().splitIntoSpectrums ().extractSound ().stopWithSounds ();
    }

    @Test
    public void subsound () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformException.class)
    public void subsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test
    public void testFilterFreqs () throws SoundTransformException {
        final Random random = new Random ();
        final float [] freqs = new float [random.nextInt (2000) + 4000];
        int i = 0;
        while (i < freqs.length) {
            final int length = Math.min (random.nextInt (200) + 400, freqs.length - i);
            final float currentFreq = random.nextInt (300) + 360;
            for (int j = 0 ; j < length ; j++) {
                freqs [i++] = currentFreq;
            }
        }
        final float [] freqsOutput = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFreqs (freqs).filterRange (0, 90).filterRange (500, 1000).stopWithFreqs ();
        for (i = 0 ; i < freqsOutput.length ; i++) {
            if (((freqsOutput [i] > 0) && (freqsOutput [i] <= 90)) || ((freqsOutput [i] >= 500) && (freqsOutput [i] <= 1000))) {
                org.junit.Assert.fail (freqsOutput [i] + " is not filtered in the freqs array (index " + i + ")");
            }
        }
    }

    @Test
    public void testImportHPSFreqs () throws SoundTransformException {
        final Random random = new Random ();
        final float [] freqs = new float [random.nextInt (2000) + 4000];
        int i = 0;
        while (i < freqs.length) {
            final int length = Math.min (random.nextInt (200) + 400, freqs.length - i);
            final float currentFreq = random.nextInt (150) + 160;
            for (int j = 0 ; j < length ; j++) {
                freqs [i++] = currentFreq;
            }
        }
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultPack.json");
        final InputStreamInfo isi = new InputStreamInfo (1, freqs.length * 100, 2, 48000, false, true);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).withFreqs (freqs).shapeIntoSound ("default", "simple_piano", isi).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void twoTimesInOneInstruction () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().andAfterStart ().withClasspathResource ("before.wav").convertIntoSound ();
    }
}
