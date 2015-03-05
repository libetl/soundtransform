package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class TestUpsample extends SoundTransformTest {

    @Test
    public void testTransform11025Hz2BitsMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] inputSounds = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input));
        final Sound [] outputSounds = new Sound [inputSounds.length];
        for (int i = 0 ; i < inputSounds.length ; i++) {
            Sound tmp = new ConvertedSoundAppender ().changeNbBytesPerSample (inputSounds [i], 2);
            tmp = new ConvertedSoundAppender ().resizeToSampleRate (tmp, 44100);
            outputSounds [i] = tmp;
        }

        final InputStream ais = $.create (TransformSoundService.class).toStream (outputSounds, new StreamInfo (outputSounds.length, outputSounds [0].getSamplesLength (), 2, 44100, false, true, null));
        $.create (ConvertAudioFileService.class).writeInputStream (ais, output);

    }

    @Test
    public void testTransform8363Hz1BitMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] inputSounds = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input));
        final Sound [] outputSounds = new Sound [inputSounds.length];
        for (int i = 0 ; i < inputSounds.length ; i++) {
            // Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds
            // [i], 2);
            final Sound tmp = new ConvertedSoundAppender ().resizeToSampleRate (inputSounds [i], 44100);
            outputSounds [i] = tmp;
        }

        final InputStream ais = $.create (TransformSoundService.class).toStream (outputSounds, new StreamInfo (outputSounds.length, outputSounds [0].getSamplesLength (), 2, 44100, false, true, null));

        $.create (ConvertAudioFileService.class).writeInputStream (ais, output);
    }
}
