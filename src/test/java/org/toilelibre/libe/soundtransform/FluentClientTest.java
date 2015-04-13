package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation.FluentClientOperationErrorCode;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.InsertPartSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReplacePartSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

import android.content.Context;

public class FluentClientTest extends SoundTransformTest {

    @Test
    public void androidImportPackDoesNotWorkInJavaxMode () throws SoundTransformException {
        try {
            FluentClient.start ().withAPack ("default", Mockito.mock (Context.class), R.raw.class, R.raw.defaultpack).stopWithAPack ("default");
            Assert.fail ("android import should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode ().name (), "STUB_IMPLEMENTATION");
        }
    }

    @Test
    public void appendTest () throws SoundTransformException {
        final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano4.wav").convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().append (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void backAndForth () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().apply (new NoOpSoundTransformation ()).exportToClasspathResource ("before.wav").convertIntoSound ();
    }

    @Test
    public void changeFormat () throws SoundTransformException {
        final FormatInfo fi = new FormatInfo (1, 8000);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().changeFormat (fi);
    }

    @Test
    public void compress () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        final float [] array2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFreqs (array1).compress (2).stopWithFreqs ();
        Assert.assertArrayEquals (new float [] { 1.0f, 3.0f, 5.0f, 7.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f }, array2, 0);
    }

    @Test
    public void cannotAskToTheFluentClientOperationToReturnSomething () throws SoundTransformException {
        try {
            FluentClientOperation.prepare ().stopWithAPack ("default");
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withFile(null).stopWithFile ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withFreqs(null).stopWithFreqs ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withAudioInputStream(null).stopWithInputStream ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithObservers ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().inParallel(null, 0, "").stopWithResults (Object.class);
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withSounds(null).stopWithSounds ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withSpectrums(null).stopWithSpectrums ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withAudioInputStream(null).stopWithStreamInfo ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
    }

    @Test
    public void canAskToDoALotOfThingsToTheFluentClientOperationAndNothingShouldBeDone () throws SoundTransformException {
        FluentClientOperation.prepare ().withAnObserver().andAfterStart().withAPack(null, new ByteArrayInputStream (new byte [0])).withAPack(null, "").withAPack(null, null, null, 0).withAudioInputStream(null).importToSound().append(null).apply(null).changeFormat(null).cutSubSound(0, 0).playIt().changeFormat(null).exportToClasspathResource(null).playIt().importToStream().playIt().importToSound().exportToClasspathResourceWithSiblingResource(null, null).convertIntoSound().exportToFile(null).convertIntoSound().exportToStream().importToSound().findLoudestFrequencies().compress(0).filterRange(0, 0).insertPart(null, 0).octaveDown().octaveUp().replacePart(null, 0).shapeIntoSound(null, null, null).loop(0).mixWith(null).splitIntoSpectrums().playIt().extractSound().build();
    }

    @Test
    public void cutsound () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().cutSubSound (100000, 600000).exportToClasspathResource ("after.wav");
    }

    @Test
    public void defaultObserversValue () throws SoundTransformException {
        FluentClient.setDefaultObservers (new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
            }

        }, new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
            }

        });
        org.junit.Assert.assertEquals (FluentClient.start ().stopWithObservers ().length, 2);

        org.junit.Assert.assertEquals (FluentClient.start ().withAnObserver (new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
            }

        }).stopWithObservers ().length, 3);

        FluentClient.setDefaultObservers ();
    }

    @Test
    public void findLoudestFreqs () throws SoundTransformException {
        org.junit.Assert.assertNotNull (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().findLoudestFrequencies ().stopWithFreqs ());
    }

    @Test
    public void getDefaultPack () throws SoundTransformException {
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json");
        final Pack pack = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).stopWithAPack ("default");
        Assert.assertNotNull (pack);
    }

    @Test
    public void insertPart1 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 15, 16, 17, 18, 5, 6, 7, 8 }, FluentClient.start ().withFreqs (array1).insertPart (array2, 4).stopWithFreqs (), 0);
    }

    @Test
    public void insertPart2 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 5, 6, 7, 15, 16, 17, 18, 8 }, FluentClient.start ().withFreqs (array1).insertPart (array2, 7).stopWithFreqs (), 0);
    }

    @Test
    public void insertPart3 () throws SoundTransformException {
        final float [] array1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        final float [] array2 = { 15, 16, 17, 18 };

        org.junit.Assert.assertArrayEquals (new float [] { 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 15, 16, 17, 18 }, FluentClient.start ().withFreqs (array1).insertPart (array2, 11).stopWithFreqs (), 0);
    }

    @Test
    public void loop () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().loop (100000).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void loopWithLessThan1ValueDoesNotWork () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().loop (0).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode ().name (), "NOT_POSITIVE_VALUE");
        }
    }

    @Test
    public void mixTest () throws SoundTransformException {
        final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("piano3e.wav").convertIntoSound ().stopWithSounds ();
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().mixWith (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    
    @Test
    public void mixTwoFilesAfterStart () throws SoundTransformException {
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        FluentClient.start ().withAMixedSound (
                FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSounds (), 
                FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().stopWithSounds ()).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }
    
    @Test
    public void mixTwoFilesInParallel () throws SoundTransformException {
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        FluentClient.start ().inParallel (
                // operations
                FluentClientOperation.prepare ().convertIntoSound ().build(),
                // timeout in seconds
                5,
                // classpath resources
                "piano1c.wav", "piano8c.wav").mixAllInOneSound ().exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void apply8BitOnTwoFilesInParallel () throws SoundTransformException {
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));

        FluentClient.start ().inParallel (
                // operations
                FluentClientOperation.prepare ().convertIntoSound ().apply (new EightBitsSoundTransformation (25)).exportToClasspathResourceWithSiblingResource ("after%1d.wav", "before.wav").build(),
                // timeout in seconds
                5,
                // classpath resources
                "piano1c.wav", "piano8c.wav");
    }

    @Test
    public void mix2SoundsWithAThirdOne () throws SoundTransformException {
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        FluentClient.start ().inParallel (
                // operations
                FluentClientOperation.prepare ().convertIntoSound ().build(),
                // timeout in seconds
                5,
                // classpath resources
                "piano1c.wav", "piano8c.wav").mixAllInOneSound ().exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void mix2SoundsWith2FreqsArrays () throws SoundTransformException {
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json");

        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        FluentClient.start ().withAPack ("default", packInputStream).withSounds (FluentClient.start ().inParallel (
                // operations
                FluentClientOperation.prepare ().convertIntoSound ().build(),
                // timeout in seconds
                5,
                // classpath resources
                "apiano3.wav", "apiano4.wav").mixAllInOneSound ().stopWithSounds ()).mixWith (FluentClient.start ().inParallel (
                        // operations
                        FluentClientOperation.prepare ().shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 44100f)).build(),
                        // timeout in seconds
                        5,
                        // classpath resources
                        this.generateRandomFreqs (), this.generateRandomFreqs ()).mixAllInOneSound ().stopWithSounds ()).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");

    }

    @Test
    public void appendDifferentFormatsImpossible () throws SoundTransformException {
        try {
            final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("piano3e.wav").convertIntoSound ().stopWithSounds ();
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano4.wav").convertIntoSound ().append (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
            Assert.fail ("append should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertSame (ste.getErrorCode (), ModifySoundService.ModifySoundServiceErrorCode.DIFFERENT_NUMBER_OF_CHANNELS);
        }
    }

    @Test
    public void noOp () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().apply (new NoOpSoundTransformation ()).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void noOpWithInsert () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().apply (new NoOpSoundTransformation ())
        .apply (new InsertPartSoundTransformation (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano4.wav").convertIntoSound ().stopWithSounds (), 12000)).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void playIt () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").playIt ().convertIntoSound ().playIt ().exportToStream ().playIt ();
        } catch (final PlaySoundException pse) {
            new Slf4jObserver ().notify ("This build environment cannot play a sound (ignoring) " + pse);
        }
    }

    @Test
    public void readFormat () throws SoundTransformException {
        final FormatInfo isInfo = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").importToStream ().stopWithStreamInfo ();
        isInfo.hashCode ();
    }

    public float [] generateRandomFreqs () {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final float [] data = new float [655];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (0, 20000);
        }
        return data;
    }

    @Test
    public void readRawInputStream () throws SoundTransformException {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [65536];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        final InputStream is = new ByteArrayInputStream (data);
        final StreamInfo isi = new StreamInfo (1, 32768, 2, 8000, false, true, null);

        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withRawInputStream (is, isi).importToSound ().exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void readStreamInfo () throws SoundTransformException {
        final FormatInfo fi = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").importToStream ().stopWithStreamInfo ();
        Assert.assertNotNull (fi);
        new Slf4jObserver ().notify (fi.toString ());
    }

    @Test
    public void replacePart () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().apply (new ReplacePartSoundTransformation (FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (600000, 700000).stopWithSounds (), 100000))
        .exportToClasspathResource ("after.wav");
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
    @Test
    public void shapeASoundTest () throws SoundTransformException {
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json");
        final FormatInfo fi = new FormatInfo (2, 48000);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).withClasspathResource ("gpiano3.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("default", "simple_piano", fi);
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
            if (freqsOutput [i] > 0 && freqsOutput [i] <= 90 || freqsOutput [i] >= 500 && freqsOutput [i] <= 1000) {
                org.junit.Assert.fail (freqsOutput [i] + " is not filtered in the freqs array (index " + i + ")");
            }
        }
    }

    @Test
    public void testStringPack () throws SoundTransformException {
        Assert.assertNotNull (FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", "{}").stopWithAPack ("default"));
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
        final InputStream packInputStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json");
        final FormatInfo fi = new FormatInfo (2, 48000);
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", packInputStream).withFreqs (freqs).shapeIntoSound ("default", "simple_piano", fi).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

    @Test
    public void twoTimesInOneInstruction () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().andAfterStart ().withClasspathResource ("before.wav").convertIntoSound ();
    }
}
