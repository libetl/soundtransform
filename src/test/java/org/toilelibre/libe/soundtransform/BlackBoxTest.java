package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFileFromAFileUsingSoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.GetInputStreamInfo;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class BlackBoxTest {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        gPiano3     = new File (this.classLoader.getResource ("notes/g-piano3.wav").getFile ());
    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void callPlaySoundFromOutside () throws SoundTransformException {
        new PlaySound ().play (new Sound [0]);
    }

    @Test
    public void callTransformFromOutside () throws SoundTransformException {
        new ExportAFileFromAFileUsingSoundTransform ().transformFile (this.input, this.output, new EightBitsSoundTransformation (25));
    }

    @Test
    public void getInputStreamInfo () throws SoundTransformException, UnsupportedAudioFileException, IOException {
        new Slf4jObserver ().notify (new GetInputStreamInfo ().getInputStreamInfo (AudioSystem.getAudioInputStream (this.gPiano3)).toString ());
    }
}
