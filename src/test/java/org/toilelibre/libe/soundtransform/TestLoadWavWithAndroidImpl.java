package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;

public class TestLoadWavWithAndroidImpl extends SoundTransformAndroidTest {

    private String iToS(final int n) {
        String result = "";
        for (int i = 3; i >= 0; i--) {
            char char1 = Character.toChars(n >> i * 8 & 255)[0];
            result = (char) char1 + result;
        }
        return result;
    }

    private int sToI(final String string) {
        char[] bytes;
        bytes = string.toCharArray();
        int total = (bytes[3] << 24) + (bytes[2] << 16) + (bytes[1] << 8) + (bytes[0]);
        return total;

    }

    private byte[] toBytes(char[] data) {
        byte[] toRet = new byte[data.length];
        for(int i = 0; i < toRet.length; i++) {
        toRet[i] = (byte) data[i];
        }
        return toRet;
        }

    @Test(expected = SoundTransformException.class)
    public void testConvertToBaosWithFileNotFound() throws SoundTransformException {
        try {
            $.select(AudioFileHelper.class).getAudioInputStream(new File("fileNotFound"));
        } catch (final SoundTransformException ste) {
            org.junit.Assert.assertEquals(AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, ((SoundTransformException) ste.getCause()).getErrorCode());
            throw ste;
        }
    }

    @Test(expected = SoundTransformException.class)
    public void testDidNotFindData() throws SoundTransformException, UnsupportedEncodingException {
        final String input = "RIFF1000WAVEfmt     " + '\1' + '\0' + '\1' + '\0' + this.iToS(48000) + this.iToS(0) + '\2' + '\0' + '\2' + '\0' + "datedate";
        try {
            FluentClient.start().withAudioInputStream(new ByteArrayInputStream(input.getBytes("UTF-8"))).importToSound();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause();
            org.junit.Assert.assertEquals("NO_DATA_SEPARATOR", ste.getErrorCode().name());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test(expected = SoundTransformException.class)
    public void testFileNotFound() throws SoundTransformException {
        try {
            FluentClient.start().withClasspathResource("fileNotFound.wav").convertIntoSound().exportToClasspathResource("after.wav");
        } catch (final SoundTransformException ste) {
            org.junit.Assert.assertEquals(FluentClient.FluentClientErrorCode.NO_FILE_IN_INPUT, ste.getErrorCode());
            throw ste;
        }
    }

    @Test
    public void testLoadWav() throws SoundTransformException {
        FluentClient.start().withClasspathResource("before.wav").convertIntoSound().exportToClasspathResource("after.wav");
    }

    @Test(expected = SoundTransformRuntimeException.class)
    public void testMissingFmtTag() throws SoundTransformException {
        final String input = "RIFF1000WAVEfmx";
        try {
            FluentClient.start().withAudioInputStream(new ByteArrayInputStream(input.getBytes("UTF-8"))).importToSound();
        } catch (final SoundTransformRuntimeException stre) {
            org.junit.Assert.assertEquals("WRONG_FORMAT_READ_VALUE", stre.getErrorCode().name());
            throw stre;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test(expected = SoundTransformException.class)
    public void testMissingWavTag() throws SoundTransformException {
        final String input = "RIFF1000WAVA";
        try {
            FluentClient.start().withAudioInputStream(new ByteArrayInputStream(input.getBytes("UTF-8"))).importToSound();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause();
            org.junit.Assert.assertEquals("NO_WAVE_HEADER", ste.getErrorCode().name());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test(expected = SoundTransformException.class)
    public void testMissingWrongEncoding() throws SoundTransformException {

        final String input = "RIFF1000WAVEfmt     " + '\0' + '\2';
        try {
            FluentClient.start().withAudioInputStream(new ByteArrayInputStream(input.getBytes("UTF-8"))).importToSound();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause();
            org.junit.Assert.assertEquals("NON_PCM_WAV", ste.getErrorCode().name());
            throw ste;
        } catch (final UnsupportedEncodingException e) {
            org.junit.Assert.fail("Should not throw an UnsupportedEncodingException");
        }
    }

    @Test
    public void importCDHEader() throws SoundTransformException {
        Assert.assertEquals(this.sToI(this.iToS(44100)), 44100);
        final String input = "RIFF1000WAVEfmt     " + '\1' + '\0' + '\1' + '\0' + this.iToS(44100) + this.iToS(0) + '\2' + '\0' + Character.toChars(16) [0] + '\0' + "data" + this.iToS(44);
        byte [] byteArray = this.toBytes(input.toCharArray());
        Sound [] sound = FluentClient.start().withAudioInputStream(new ByteArrayInputStream(byteArray)).importToSound().stopWithSounds();
        Assert.assertNotNull(sound);
    }

    @Test(expected = SoundTransformException.class)
    public void testNotRiffFile() throws SoundTransformException {
        try {
            FluentClient.start().withClasspathResource("defaultpackjavax.json").importToStream();
        } catch (final SoundTransformRuntimeException stre) {
            final SoundTransformException ste = (SoundTransformException) stre.getCause();
            org.junit.Assert.assertEquals("NO_MAGIC_NUMBER", ste.getErrorCode().name());
            throw ste;
        }
    }
}
