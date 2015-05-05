package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;

@PrepareForTest ({ JavazoomAudioFileHelper.class, File.class })
public class JavazoomAudioFileHelperTest {
    
    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileIOException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (new File ("notAFile"));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamUnsupportedAudioFileException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.WRONG_TYPE, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileUnsupportedAudioFileException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (new File (Thread.currentThread ().getContextClassLoader ().getResource ("defaultpack.json").getFile ()));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.WRONG_TYPE, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void createSoundTransformTempFileIOException () throws SoundTransformException {
        PowerMockito.mockStatic (File.class, new Answer<File> (){

            @Override
            public File answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException ("Unable to create temporary file");
            }
            
        });
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (new File (Thread.currentThread ().getContextClassLoader ().getResource ("mp3test.mp3").getFile ()));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CREATE_A_TEMP_FILE, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void writeInputStreamNotAudioInputStream () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().writeInputStream (new ByteArrayInputStream ("".getBytes ()), File.createTempFile ("soundtransform", "wav"));
        } catch (IOException e) {
            throw new RuntimeException (e);
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        }
    }
}
