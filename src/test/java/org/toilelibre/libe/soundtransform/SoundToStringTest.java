package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class SoundToStringTest {

    @Test
    public void testFsToString () {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("before.wav").getFile ());
        try {
            final AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
            final Sound s = new TransformSoundService ().fromInputStream (ais) [0];
            new SimpleFrequencySoundTransformation () {

                @Override
                public Spectrum transformFrequencies (final Spectrum fs) {
                    System.out.println (fs);
                    return super.transformFrequencies (fs);
                }

            }.transform (s);
        } catch (final UnsupportedAudioFileException e) {
            e.printStackTrace ();
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }

    @Test
    public void testToString () {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("before.wav").getFile ());
        try {
            final AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
            System.out.println (new TransformSoundService ().fromInputStream (ais) [0]);
        } catch (final UnsupportedAudioFileException e) {
            e.printStackTrace ();
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }
}
