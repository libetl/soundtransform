package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.PlaySoundClipImpl;
import org.toilelibre.libe.soundtransform.model.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class TestPlaySound {
    private final ClassLoader    classLoader    = Thread.currentThread ().getContextClassLoader ();
    private final File        input        = new File (this.classLoader.getResource ("before.wav").getFile ());

    @Test
    public void playBeforeWav () throws UnsupportedAudioFileException, IOException, PlaySoundException{
        final PlaySoundService ps = new PlaySoundClipImpl ();
        final ConvertAudioFileService convertAudioFileService = new ConvertAudioFileService ();
        final AudioInputStream ais = convertAudioFileService.callConverter (this.input);
        try {
            ps.play (ais);
        }catch (final java.lang.IllegalArgumentException iae){
            if (!"No line matching interface Clip is supported.".equals(iae.getMessage())){
                throw iae;
            }
        }
    }
}
