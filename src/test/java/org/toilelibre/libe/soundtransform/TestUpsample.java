package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class TestUpsample extends SoundTransformTest {

    @Test
    public void testTransform11025Hz2BitsMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano2d.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] inputSounds = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input));
        final Sound [] outputSounds = new Sound [inputSounds.length];
        for (int i = 0 ; i < inputSounds.length ; i++) {
            Sound tmp = $.select (SoundAppender.class).changeNbBytesPerSample (inputSounds [i], 2);
            tmp = $.select (SoundAppender.class).resizeToSampleRate (tmp, 44100);
            outputSounds [i] = tmp;
        }

        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (outputSounds, new StreamInfo (outputSounds.length, outputSounds [0].getSamplesLength (), 2, 44100, false, true, null));
        $.select (AudioFileService.class).fileFromStream (ais, output);

    }

    @Test
    public void testTransform8363Hz1BitMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("gpiano3.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] inputSounds = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input));
        final Sound [] outputSounds = new Sound [inputSounds.length];
        for (int i = 0 ; i < inputSounds.length ; i++) {
            // Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds
            // [i], 2);
            final Sound tmp = $.select (SoundAppender.class).resizeToSampleRate (inputSounds [i], 44100);
            outputSounds [i] = tmp;
        }

        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (outputSounds, new StreamInfo (outputSounds.length, outputSounds [0].getSamplesLength (), 2, 44100, false, true, null));

        $.select (AudioFileService.class).fileFromStream (ais, output);
    }
}
