package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;

public class TestUpsample extends SoundTransformTest {

    @Test
    public void testTransform11025Hz2BitsMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano2d.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound inputSound = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input));
        final Channel [] outputChannels = new Channel [inputSound.getNumberOfChannels ()];
        for (int i = 0 ; i < inputSound.getNumberOfChannels () ; i++) {
            Channel tmp = $.select (SoundAppender.class).changeNbBytesPerSample (inputSound.getChannels () [i], 2);
            tmp = $.select (SoundAppender.class).resizeToSampleRate (tmp, 44100);
            outputChannels [i] = tmp;
        }

        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (outputChannels), new StreamInfo (outputChannels.length, outputChannels [0].getSamplesLength (), 2, 44100, false, true, null));
        $.select (AudioFileService.class).fileFromStream (ais, output);

    }

    @Test
    public void testTransform8363Hz1BitMonoSoundInto44100Hz2BitsMonoSound () throws SoundTransformException, IOException {

        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("gpiano3.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound inputSound = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input));
        final Channel [] outputChannels = new Channel [inputSound.getNumberOfChannels ()];
        for (int i = 0 ; i < inputSound.getNumberOfChannels () ; i++) {
            // Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds
            // [i], 2);
            final Channel tmp = $.select (SoundAppender.class).resizeToSampleRate (inputSound.getChannels () [i], 44100);
            outputChannels [i] = tmp;
        }

        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (outputChannels), new StreamInfo (outputChannels.length, outputChannels [0].getSamplesLength (), 2, 44100, false, true, null));

        $.select (AudioFileService.class).fileFromStream (ais, output);
    }
}
