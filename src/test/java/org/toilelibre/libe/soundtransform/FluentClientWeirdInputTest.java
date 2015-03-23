package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient.FluentClientErrorCode;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ReplacePartSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class FluentClientWeirdInputTest {


    @Test (expected = SoundTransformException.class)
    public void cutsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformRuntimeException.class)
    public void replacePartOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().apply (new ReplacePartSoundTransformation (FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (600000, 700000).stopWithSounds (), -100000))
        .exportToClasspathResource ("after.wav");
    }

    @Test (expected = SoundTransformException.class)
    public void subsoundOutOfBounds () throws SoundTransformException {
        FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource ("before.wav").convertIntoSound ().extractSubSound (-100000, 200000).exportToClasspathResource ("after.wav");
    }
    
    @Test
    public void extractSoundWithNoSpectrum () throws SoundTransformException {
        try {
            FluentClient.start ().withSpectrums (null).extractSound ();
            Assert.fail ("Should have failed with a null spectrum object");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
        try {
            FluentClient.start ().withSpectrums (new ArrayList<Spectrum<Serializable> []> ()).extractSound ();
            Assert.fail ("Should have failed with an empty spectrum object");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
        try {
            List<Spectrum<Serializable> []> list = new ArrayList<Spectrum<Serializable> []> ();
            @SuppressWarnings ("unchecked")
            Spectrum<Serializable> [] emptySpectrumArray = new Spectrum [0];
            list.add (emptySpectrumArray);
            FluentClient.start ().withSpectrums (list).extractSound ();
            Assert.fail ("Should have failed with a empty spectrum array");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_SPECTRUM_IN_INPUT);
        }
    }
    

    @Test
    public void importToSoundOfANullInputStream () throws SoundTransformException {
        try {
            FluentClient.start ().withAudioInputStream (null).importToSound ();
            Assert.fail ("Should have failed with a null input stream");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.INPUT_STREAM_NOT_READY);
        }
    }    

    @Test
    public void importToSoundOfANullFile () throws SoundTransformException {
        try {
            FluentClient.start ().withFile (null).importToStream ();
            Assert.fail ("Should have failed with a null file");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NO_FILE_IN_INPUT);
        }
    }
    
    @Test
    public void noClasspathResourceFolderUnknown () throws SoundTransformException {
        try {
            File f = new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ());
            FluentClient.start ().withFile (f).convertIntoSound ().exportToClasspathResource ("after.wav");
            Assert.fail ("Should have failed with an unknown classpath resource folder");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE);
        }
    }
    


    @Test
    public void nullInputStreamToAFile () throws SoundTransformException {
        try {
            File f = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParentFile ().getPath () + "/after.wav");
            FluentClient.start ().withAudioInputStream (null).writeToFile (f);
            Assert.fail ("Should have failed with a null input stream");
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), FluentClientErrorCode.NOTHING_TO_WRITE);
        }
    }    
}
