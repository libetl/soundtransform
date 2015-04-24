package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReplacePartSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
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
            final Sound [] sounds2 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("piano3e.wav").convertIntoSound ().stopWithSound ();
            FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("gpiano4.wav").convertIntoSound ().append (sounds2).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
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
            FluentClientOperation.prepare ().stopWithAPack ("default");
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withFile (null).stopWithFile ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withFreqs (null).stopWithFreqs ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withAudioInputStream (null).stopWithInputStream ();
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
            FluentClientOperation.prepare ().inParallel (null, 0, "").stopWithResults (Object.class);
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withSound (null).stopWithSound ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withSpectrums (null).stopWithSpectrums ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
        }
        try {
            FluentClientOperation.prepare ().withAudioInputStream (null).stopWithStreamInfo ();
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException ste) {
            Assert.assertEquals (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, ste.getErrorCode ());
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

}
