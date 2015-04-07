package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

@PrepareForTest ({ AndroidAudioFileHelper.class, FileInputStream.class })
public class AndroidAudioFileHelperTest extends SoundTransformAndroidTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule ();

    @Test
    public void convertFileToBaosFileNotFound () {
        this.powerMockRule.hashCode ();
        try {
            new AndroidAudioFileHelper ().convertFileToBaos (new File (""));
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM);
        }
    }

    @Test
    public void convertFileToBaosIOException () throws Exception {
        try {
            final FileInputStream is = Mockito.mock (FileInputStream.class);
            PowerMockito.whenNew (FileInputStream.class).withAnyArguments ().thenReturn (is);
            Mockito.when (is.read (Mockito.any (byte [].class))).thenThrow (new IOException ("Mocked IO Exception"));
            Mockito.doThrow (new IOException ("Mocked IO Exception")).when (is).close ();
            new AndroidAudioFileHelper ().convertFileToBaos (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()));
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.COULD_NOT_CONVERT);
        }
    }

    @Test
    public void writeInputStreamWrongInputStreamFormat () {
        try {
            new AndroidAudioFileHelper ().writeInputStream (new ByteArrayInputStream (new byte [0]), new File (""));
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ);
        }
    }

    @Test
    public void toStreamNotStreamInfo () {
        try {
            new AndroidAudioFileHelper ().toStream (new byte [0], new Object ());
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ);
        }
    }

    @Test
    public void writeInputStreamWrongFileNotFound () {
        try {
            new AndroidAudioFileHelper ().writeInputStream (new ByteArrayWithAudioFormatInputStream (new byte [0], new StreamInfo (2, 10000, 2, 44100, false, true, null)), new File (""));
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.COULD_NOT_CREATE_AN_OUTPUT_FILE);
        }
    }

    @Test
    public void writeInputStreamIOException () throws Exception {
        try {
            final WavOutputStream wos = Mockito.mock (WavOutputStream.class);
            PowerMockito.whenNew (WavOutputStream.class).withAnyArguments ().thenReturn (wos);
            Mockito.doThrow (new IOException ("Mocked IO Exception")).when (wos).close ();
            Mockito.doThrow (new IOException ("Mocked IO Exception")).when (wos).write (Mockito.any (byte [].class));
            new AndroidAudioFileHelper ().writeInputStream (new ByteArrayWithAudioFormatInputStream (new byte [0], new StreamInfo (2, 10000, 2, 44100, false, true, null)), new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()));
            Assert.fail ("Should have thrown an exception here");
        } catch (final SoundTransformException e) {
            Assert.assertEquals (e.getErrorCode (), AudioFileHelperErrorCode.COULD_NOT_CONVERT);
        }
    }

}
