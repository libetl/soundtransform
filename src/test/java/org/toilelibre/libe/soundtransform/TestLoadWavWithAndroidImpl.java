package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AndroidAudioFileHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AndroidWavHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AudioInputStream.AudioInputStreamErrorCode;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;

public class TestLoadWavWithAndroidImpl extends SoundTransformAndroidTest {

    byte [] intToByteArray (final int n) {
        final byte [] b = new byte [4];
        for (int i = b.length - 1 ; i >= 0 ; i--) {
            b [b.length - 1 - i] = (byte) (n >> (i * 8));
        }
        return b;
    }

    private String itoS (int i) throws UnsupportedEncodingException {
        return new String (this.intToByteArray (i), "UTF-8").toString ();
    }

    @Test (expected = SoundTransformException.class)
    public void testDidNotFindData () throws SoundTransformException, UnsupportedEncodingException {
        final String input = "RIFF1000WAVEfmt     " + '\1' + '\0' + '\1' + '\0' + this.itoS (48000) + this.itoS (0) + '\2' + '\0' + '\2' + '\0' + "datedate";
        try {
            FluentClient.start ().withAudioInputStream (new ByteArrayInputStream (input.getBytes ("UTF-8"))).importToSound ();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause ();
            org.junit.Assert.assertEquals (AndroidWavHelper.AudioWavHelperErrorCode.NO_DATA_SEPARATOR, ste.getErrorCode ());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail ("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test
    public void testLoadWav () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void testMissingFmtTag () throws SoundTransformException {
        final String input = "RIFF1000WAVEfmx";
        try {
            FluentClient.start ().withAudioInputStream (new ByteArrayInputStream (input.getBytes ("UTF-8"))).importToSound ();
        } catch (final SoundTransformRuntimeException stre) {
            org.junit.Assert.assertEquals (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, stre.getErrorCode ());
            throw stre;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail ("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test (expected = SoundTransformException.class)
    public void testMissingWavTag () throws SoundTransformException {
        final String input = "RIFF1000WAVA";
        try {
            FluentClient.start ().withAudioInputStream (new ByteArrayInputStream (input.getBytes ("UTF-8"))).importToSound ();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause ();
            org.junit.Assert.assertEquals (AndroidWavHelper.AudioWavHelperErrorCode.NO_WAVE_HEADER, ste.getErrorCode ());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail ("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test (expected = SoundTransformException.class)
    public void testMissingWrongEncoding () throws SoundTransformException {
        final String input = "RIFF1000WAVEfmt     " + '\0' + '\2';
        try {
            FluentClient.start ().withAudioInputStream (new ByteArrayInputStream (input.getBytes ("UTF-8"))).importToSound ();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause ();
            org.junit.Assert.assertEquals (AndroidWavHelper.AudioWavHelperErrorCode.NON_PCM_WAV, ste.getErrorCode ());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail ("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test (expected = SoundTransformException.class)
    public void testNotRiffFile () throws SoundTransformException {
        try {
            FluentClient.start ().withClasspathResource ("defaultPack.json").importToStream ();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause ();
            org.junit.Assert.assertEquals (AndroidWavHelper.AudioWavHelperErrorCode.NO_MAGIC_NUMBER, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test(expected=SoundTransformException.class)
    public void testFileNotFound () throws SoundTransformException {
        try  {
        FluentClient.start ().withClasspathResource ("fileNotFound.wav").convertIntoSound ().exportToClasspathResource ("after.wav");
        }catch (SoundTransformException ste){
            org.junit.Assert.assertEquals (FluentClient.FluentClientErrorCode.NO_FILE_IN_INPUT, ste.getErrorCode ());
            throw ste;
        }
    }
    @Test(expected=SoundTransformException.class)
    public void testConvertToBaosWithFileNotFound () throws SoundTransformException {
        try  {
            new AndroidAudioFileHelper ().convertFileToBaos (new File ("fileNotFound"));
        }catch (SoundTransformException ste){
            org.junit.Assert.assertEquals (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }
}
