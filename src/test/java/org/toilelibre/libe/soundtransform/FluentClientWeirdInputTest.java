package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient.FluentClientErrorCode;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation.FluentClientOperationErrorCode;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService.CallTransformServiceErrorCode;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReplacePartSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransform.ShapeSoundTransformErrorCode;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService.ImportPackServiceErrorCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

import android.content.Context;

public class FluentClientWeirdInputTest extends SoundTransformTest {

    @Test
    public void androidImportPackDoesNotWorkInJavaxMode () throws SoundTransformException {
        try {
            FluentClient.start ().withAPack ("default", Mockito.mock (Context.class), R.raw.class, R.raw.defaultpack).stopWithAPack ("default");
            Assert.fail ("android import should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode ().name (), "STUB_IMPLEMENTATION");
        }
    }

    @Test (expected = SoundTransformException.class)
    public void cutsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void replacePartOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().apply (new ReplacePartSoundTransform (FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (600000, 700000).stopWithSound (), -100000))
        .exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformException.class)
    public void subsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformException.class)
    public void appendDifferentFormatsImpossible () throws SoundTransformException {
        try {
            final Sound sound2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("piano3e.wav").convertIntoSound ().stopWithSound ();
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano4.wav").convertIntoSound ().append (sound2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
            Assert.fail ("append should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertSame (ste.getErrorCode (), ModifySoundService.ModifySoundServiceErrorCode.DIFFERENT_NUMBER_OF_CHANNELS);
            throw ste;
        }
    }

    @Test
    public void extractSoundWithNoSpectrum () throws SoundTransformException {
        try {
            FluentClient.start ().withSpectrums (null).extractSound ();
            Assert.fail ("Should have failed with a null spectrum object");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
        try {
            FluentClient.start ().withSpectrums (new ArrayList<Spectrum<Serializable> []> ()).extractSound ();
            Assert.fail ("Should have failed with an empty spectrum object");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
        try {
            final List<Spectrum<Serializable> []> list = new ArrayList<Spectrum<Serializable> []> ();
            @SuppressWarnings ("unchecked")
            final Spectrum<Serializable> [] emptySpectrumArray = new Spectrum [0];
            list.add (emptySpectrumArray);
            FluentClient.start ().withSpectrums (list).extractSound ();
            Assert.fail ("Should have failed with a empty spectrum array");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
    }

    @Test
    public void importToSoundOfANullInputStream () throws SoundTransformException {
        try {
            FluentClient.start ().withAudioInputStream (null).importToSound ();
            Assert.fail ("Should have failed with a null input stream");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.INPUT_STREAM_NOT_READY);
        }
    }

    @Test
    public void importToSoundOfANullFile () throws SoundTransformException {
        try {
            FluentClient.start ().withFile (null).importToStream ();
            Assert.fail ("Should have failed with a null file");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_FILE_IN_INPUT);
        }
    }

    @Test
    public void noClasspathResourceFolderUnknown () throws SoundTransformException {
        try {
            final File f = new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ());
            FluentClient.start ().withFile (f).convertIntoSound ().exportToClasspathResource ("after.wav");
            Assert.fail ("Should have failed with an unknown classpath resource folder");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE);
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void nothingInInput () throws SoundTransformException {
        try {
            FluentClient.start ().withSound (new Sound (new Channel [0])).apply (new EightBitsSoundTransform (25));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (CallTransformServiceErrorCode.NOTHING_IN_INPUT, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test
    public void nullInputStreamToAFile () throws SoundTransformException {
        try {
            final File f = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParentFile ().getPath () + "/after.wav");
            FluentClient.start ().withAudioInputStream (null).writeToFile (f);
            Assert.fail ("Should have failed with a null input stream");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NOTHING_TO_WRITE);
        }
    }

    @Test
    public void cannotAskToTheFluentClientOperationToReturnSomething () throws SoundTransformException {
        try {
            FluentClientOperation.prepare ().andAfterStart ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RESTART_IN_AN_OPERATION, ste.getErrorCode ());
        }

        try {
            FluentClientOperation.prepare ().mixAllInOneSound ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RESTART_IN_AN_OPERATION, ste.getErrorCode ());
        }

        try {
            FluentClientOperation.prepare ().applyAndStop (new NoOpSoundTransform ());
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithAPack ("default");
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithFile ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithFreqs ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithInputStream ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithObservers ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithSound ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithSpectrums ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().stopWithStreamInfo ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, ste.getErrorCode ());
        }

    }

    @Test (expected = SoundTransformException.class)
    public void loopWithLessThan1ValueDoesNotWork () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().loop (0).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode ().name (), "NOT_POSITIVE_VALUE");
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void mixAllInOneSoundOnlyWorksWithSounds () throws SoundTransformException {
        FluentClient.start ()
        .inParallel (FluentClientOperation.prepare ().importToStream ().build (), 5, new File (Thread.currentThread ().getContextClassLoader ().getResource ("piano1c.wav").getFile ()), new File (Thread.currentThread ().getContextClassLoader ().getResource ("piano8c.wav").getFile ()))
        .mixAllInOneSound ();
    }

    @Test
    public void canAskToDoALotOfThingsToTheFluentClientOperationAndNothingShouldBeDone () throws SoundTransformException {
        FluentClientOperation.prepare ().importToSound ().append (null).apply (null).changeFormat (null).cutSubSound (0, 0).playIt ().changeFormat (null).exportToClasspathResource (null).playIt ().importToStream ().playIt ().importToSound ().exportToClasspathResourceWithSiblingResource (null, null)
        .convertIntoSound ().exportToFile (null).convertIntoSound ().exportToStream ().importToSound ().findLoudestFrequencies ().compress (0).filterRange (0, 0).insertPart (null, 0).octaveDown ().octaveUp ().replacePart (null, 0).shapeIntoSound (null, null, null).loop (0).mixWith (null)
        .splitIntoSpectrums ().playIt ().extractSound ();

    }

    @Test (expected = SoundTransformException.class)
    public void shapeWithoutPack () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("apack", "anInstrument", new FormatInfo (2, 44100));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ShapeSoundTransformErrorCode.NO_PACK_IN_PARAMETER, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void shapeWithNullFreqs () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFreqs (Collections.<float []> singletonList (null)).shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 44100));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ShapeSoundTransformErrorCode.NO_LOUDEST_FREQS_IN_ATTRIBUTE, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void shapeNotAnInstrument () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFreqs (Collections.<float []> singletonList (new float [] { 120, 120, 120, 120, 120 })).shapeIntoSound ("default", "notaninstrument", new FormatInfo (2, 44100));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ShapeSoundTransformErrorCode.NOT_AN_INSTRUMENT, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void importEmptyPack () throws SoundTransformException {
        try {
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("nullInputStreamPack", (InputStream) null);
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ImportPackServiceErrorCode.EMPTY_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void importInvalidPack () throws SoundTransformException {
        final InputStream is = Mockito.mock (InputStream.class);
        try {
            Mockito.when (is.read (Mockito.any (byte [].class))).thenThrow (new IOException ("Could not read from InputStream"));
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("nullInputStreamPack", is);
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ImportPackServiceErrorCode.INVALID_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        } catch (final IOException e) {
            throw new RuntimeException (e);
        }
    }

    @Test
    public void importANonExistingTechnicalInstrument () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withAPack ("wrongtechnicalinstrument", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("wrongtechnicalinstrument.json"));
    }
    
    @Test (expected = SoundTransformException.class)
    public void cutSoundBeforeZero () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().cutSubSound (-1000, 1000);
    }

    @Test (expected = SoundTransformException.class)
    public void cutSoundStartAfterEnd () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().cutSubSound (2000, 1000);
    }

    @Test (expected = SoundTransformException.class)
    public void cutSoundEndOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano3.wav").convertIntoSound ().cutSubSound (3000, 300000);
    }
}