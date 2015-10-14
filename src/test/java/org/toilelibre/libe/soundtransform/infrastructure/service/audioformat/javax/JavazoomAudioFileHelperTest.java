package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

@PrepareForTest ({ JavazoomAudioFileHelper.class, File.class })
public class JavazoomAudioFileHelperTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileIOException () throws SoundTransformException {
        this.rule.hashCode ();
        try {
            new JavazoomAudioFileHelper ().getUnknownInputStreamFromFile (new File ("notAFile"));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamNotAMP3FileWithMP3Ext () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("notamp3file.mp3"));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.CUSTOM_CONVERSION_FAILED, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamFromMP3IOException () throws SoundTransformException {
        InputStream is = null;
        try {
            is = new FileInputStream (File.createTempFile ("soundtransform", "wav"));
            new JavazoomAudioFileHelper ().getAudioInputStream (is);
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        } catch (final Exception e) {
            throw new RuntimeException (e);
        } finally {
            if (is != null) {
                try {
                    is.close ();
                } catch (final IOException e) {
                    new Slf4jObserver (LogLevel.ERROR).notify ("warn : exception when closing a stream, in a test " + e);
                }
            }
        }
    }

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileUnsupportedAudioFileException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.WRONG_TYPE, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamUnsupportedAudioFileException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.WRONG_TYPE, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void writeInputStreamBadAudioInputStream () throws SoundTransformException {
        final InputStream ais = new JavazoomAudioFileHelper ().getUnknownInputStreamFromFile (new File (Thread.currentThread ().getContextClassLoader ().getResource ("mp3test.mp3").getFile ()));
        try {
            new JavazoomAudioFileHelper ().writeInputStream (ais, new File ("sftp://notafile"));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void writeInputStreamNotAudioInputStream () throws SoundTransformException {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream ("".getBytes ("UTF-8"));
            new JavazoomAudioFileHelper ().writeInputStream (is, File.createTempFile ("soundtransform", "wav"));
        } catch (final IOException e) {
            throw new RuntimeException (e);
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        } finally {
            if (is != null) {
                try {
                    is.close ();
                } catch (final IOException e) {
                    new Slf4jObserver (LogLevel.ERROR).notify ("warn : exception when closing a stream, in a test " + e);
                }
            }
        }
    }

    @Test (expected = SoundTransformException.class)
    public void toStreamButNoAudioFormat () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().toStream (new byte [0], "notAnAudioFormatObject");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, ste.getErrorCode ());
            throw ste;
        }
    }
}
