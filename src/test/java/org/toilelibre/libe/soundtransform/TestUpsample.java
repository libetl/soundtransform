package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class TestUpsample {

    @Test
    public void testTransform11025Hz2BitsMonoSoundInto44100Hz2BitsMonoSound () {

        try {
            final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
            final File input = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
            final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
            final Sound [] inputSounds = new TransformSoundService ().fromInputStream (new ConvertAudioFileService ().callConverter (input));
            final Sound [] outputSounds = new Sound [inputSounds.length];
            for (int i = 0 ; i < inputSounds.length ; i++) {
                Sound tmp = new ConvertedSoundAppender ().changeNbBytesPerSample (inputSounds [i], 2);
                tmp = new ConvertedSoundAppender ().resizeToSampleRate (tmp, 44100);
                outputSounds [i] = tmp;
            }

            final AudioInputStream ais = new TransformSoundService ().toStream (outputSounds, new AudioFormat (44100, 2 * 8, outputSounds.length, true, false));

            AudioSystem.write (ais, AudioFileFormat.Type.WAVE, output);

        } catch (final UnsupportedAudioFileException e) {
            e.printStackTrace ();
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }

    @Test
    public void testTransform8363Hz1BitMonoSoundInto44100Hz2BitsMonoSound () {

        try {
            final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
            final File input = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
            final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
            final Sound [] inputSounds = new TransformSoundService ().fromInputStream (new ConvertAudioFileService ().callConverter (input));
            final Sound [] outputSounds = new Sound [inputSounds.length];
            for (int i = 0 ; i < inputSounds.length ; i++) {
                // Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds
                // [i], 2);
                final Sound tmp = new ConvertedSoundAppender ().resizeToSampleRate (inputSounds [i], 44100);
                outputSounds [i] = tmp;
            }

            final AudioInputStream ais = new TransformSoundService ().toStream (outputSounds, new AudioFormat (44100, 1, outputSounds.length, true, false));

            AudioSystem.write (ais, AudioFileFormat.Type.WAVE, output);

        } catch (final UnsupportedAudioFileException e) {
            e.printStackTrace ();
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }
}
