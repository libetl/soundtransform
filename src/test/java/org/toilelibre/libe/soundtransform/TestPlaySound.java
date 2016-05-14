package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

public class TestPlaySound extends SoundTransformTest {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());

    @Test
    public void playBeforeWav () throws SoundTransformException {
        final PlayObjectProcessor ps = $.select (PlayObjectProcessor.class);
        final AudioFileService<?> convertAudioFileService = $.select (AudioFileService.class);
        final InputStream ais = convertAudioFileService.streamFromFile (this.input);
        final StreamInfo streamInfo = $.select (InputStreamToSoundService.class).getStreamInfo (ais);
        final Object monitor = new Object ();
        try {
            ps.play (ais, streamInfo, monitor, 0);
        } catch (final java.lang.IllegalArgumentException iae) {
            if (!"No line matching interface Clip is supported.".equals (iae.getMessage ())) {
                throw iae;
            }
        } catch (final PlayObjectException e) {
            // javax.sound.sampled.LineUnavailableException for some JDK
            // versions
            if (!javax.sound.sampled.LineUnavailableException.class.equals (e.getCause ().getClass ()) && !java.lang.IllegalArgumentException.class.equals (e.getCause ().getClass ())) {
                throw e;
            }
        } catch (final RuntimeException e) {
            if (!"Stub!".equals (e.getMessage ())) {
                throw e;
            }
        }
        synchronized (monitor) {
            try {
                monitor.wait ();
            } catch (InterruptedException e) {
                throw new RuntimeException (e);
            }
        }
    }
    
    @Test
    public void playBeforeWavAndStopAlmostImmediately () throws SoundTransformException {
        final PlayObjectProcessor ps = $.select (PlayObjectProcessor.class);
        final AudioFileService<?> convertAudioFileService = $.select (AudioFileService.class);
        final InputStream ais = convertAudioFileService.streamFromFile (this.input);
        final StreamInfo streamInfo = $.select (InputStreamToSoundService.class).getStreamInfo (ais);
        Object monitor = new Object ();
        try {
            ps.play (ais, streamInfo, monitor, 0);
            try {
                Thread.sleep (1000);
            } catch (InterruptedException e) {
            }
            synchronized (monitor) {
                monitor.notifyAll ();
            }
        } catch (final java.lang.IllegalArgumentException iae) {
            if (!"No line matching interface Clip is supported.".equals (iae.getMessage ())) {
                throw iae;
            }
        } catch (final PlayObjectException e) {
            // javax.sound.sampled.LineUnavailableException for some JDK
            // versions
            if (!javax.sound.sampled.LineUnavailableException.class.equals (e.getCause ().getClass ()) && !java.lang.IllegalArgumentException.class.equals (e.getCause ().getClass ())) {
                throw e;
            }
        } catch (final RuntimeException e) {
            if (!"Stub!".equals (e.getMessage ())) {
                throw e;
            }
        }
    }
    
    @Test
    public void playBeforeWavSkip15secondsAndStopAlmostImmediately () throws SoundTransformException {
        final PlayObjectProcessor ps = $.select (PlayObjectProcessor.class);
        final AudioFileService<?> convertAudioFileService = $.select (AudioFileService.class);
        final InputStream ais = convertAudioFileService.streamFromFile (this.input);
        final StreamInfo streamInfo = $.select (InputStreamToSoundService.class).getStreamInfo (ais);
        Object monitor = new Object ();
        try {
            ps.play (ais, streamInfo, monitor, 15000);
            try {
                Thread.sleep (2000);
            } catch (InterruptedException e) {
            }
            synchronized (monitor) {
                monitor.notifyAll ();
            }
        } catch (final java.lang.IllegalArgumentException iae) {
            if (!"No line matching interface Clip is supported.".equals (iae.getMessage ())) {
                throw iae;
            }
        } catch (final PlayObjectException e) {
            // javax.sound.sampled.LineUnavailableException for some JDK
            // versions
            if (!javax.sound.sampled.LineUnavailableException.class.equals (e.getCause ().getClass ()) && !java.lang.IllegalArgumentException.class.equals (e.getCause ().getClass ())) {
                throw e;
            }
        } catch (final RuntimeException e) {
            if (!"Stub!".equals (e.getMessage ())) {
                throw e;
            }
        }
    }
    
    @Test
    public void playBeforeWavSkip20secondsAndStopAlmostImmediately () throws SoundTransformException {
        final PlayObjectProcessor ps = $.select (PlayObjectProcessor.class);
        final AudioFileService<?> convertAudioFileService = $.select (AudioFileService.class);
        final InputStream ais = convertAudioFileService.streamFromFile (this.input);
        final StreamInfo streamInfo = $.select (InputStreamToSoundService.class).getStreamInfo (ais);
        Object monitor = new Object ();
        try {
            ps.play (ais, streamInfo, monitor, 20000);
            try {
                Thread.sleep (1000);
            } catch (InterruptedException e) {
            }
            synchronized (monitor) {
                monitor.notifyAll ();
            }
        } catch (final java.lang.IllegalArgumentException iae) {
            if (!"No line matching interface Clip is supported.".equals (iae.getMessage ())) {
                throw iae;
            }
        } catch (final PlayObjectException e) {
            // javax.sound.sampled.LineUnavailableException for some JDK
            // versions
            if (!javax.sound.sampled.LineUnavailableException.class.equals (e.getCause ().getClass ()) && !java.lang.IllegalArgumentException.class.equals (e.getCause ().getClass ())) {
                throw e;
            }
        } catch (final RuntimeException e) {
            if (!"Stub!".equals (e.getMessage ())) {
                throw e;
            }
        }
    }
}
