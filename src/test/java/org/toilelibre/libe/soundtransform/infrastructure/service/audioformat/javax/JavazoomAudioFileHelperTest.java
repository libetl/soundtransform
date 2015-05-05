package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

@PrepareForTest ({ JavazoomAudioFileHelper.class, File.class, MpegAudioFileReader.class })
public class JavazoomAudioFileHelperTest {
    
    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileIOException () throws SoundTransformException {
        rule.hashCode ();
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (new File ("notAFile"));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamNotAMP3FileWithMP3Ext () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (new File (Thread.currentThread ().getContextClassLoader ().getResource ("notamp3file.mp3").getFile ()));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.WRONG_TYPE, ste.getErrorCode ());
            throw ste;
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavFileFromMP3IOException () throws SoundTransformException {
        try {
            MpegAudioFileReader mock = Mockito.mock (MpegAudioFileReader.class);
            Mockito.when (mock.getAudioInputStream (Mockito.any (File.class))).thenThrow (new IOException ());
            PowerMockito.whenNew (MpegAudioFileReader.class).withNoArguments().thenReturn (mock);
            new JavazoomAudioFileHelper ().getAudioInputStream (new File (Thread.currentThread ().getContextClassLoader ().getResource ("mp3test.mp3").getFile ()));
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
    
    @Test (expected = SoundTransformException.class)
    public void getAudioInputSreamFromWavInputStreamFromMP3IOException () throws SoundTransformException {
        InputStream is = null;
        try {
            is = new FileInputStream (File.createTempFile ("soundtransform", "wav"));
            new JavazoomAudioFileHelper ().getAudioInputStream (is);
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        } catch (Exception e) {
            throw new RuntimeException (e);
        } finally {
            if (is != null) {
                  try {
                    is.close ();
                } catch (IOException e) {
                    new Slf4jObserver (LogLevel.ERROR).notify ("warn : exception when closing a stream, in a test " + e);
                }
            }
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
    public void getAudioInputSreamFromWavInputStreamUnsupportedAudioFileException () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().getAudioInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
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
    public void writeInputStreamBadAudioInputStream () throws SoundTransformException {
        InputStream ais = new JavazoomAudioFileHelper ().getAudioInputStream (new File (Thread.currentThread ().getContextClassLoader ().getResource ("mp3test.mp3").getFile ()));
        try {
            new JavazoomAudioFileHelper ().writeInputStream (ais, 
                new File ("sftp://notafile"));
        } catch (SoundTransformException ste) {
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
        } catch (IOException e) {
            throw new RuntimeException (e);
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste.getErrorCode ());
            throw ste;
        } finally {
            if (is != null) {
                try {
                  is.close ();
              } catch (IOException e) {
                  new Slf4jObserver (LogLevel.ERROR).notify ("warn : exception when closing a stream, in a test " + e);
              }
          }
      }
    }
    
    @Test (expected = SoundTransformException.class)
    public void toStreamButNoAudioFormat () throws SoundTransformException {
        try {
            new JavazoomAudioFileHelper ().toStream (new byte [0], "notAnAudioFormatObject");
        } catch (SoundTransformException ste) {
            Assert.assertEquals (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, ste.getErrorCode ());
            throw ste;
        }
    }
}
