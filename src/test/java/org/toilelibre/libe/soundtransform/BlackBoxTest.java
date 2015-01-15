package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFileFromAFileUsingSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class BlackBoxTest {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private File              input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void callTransformFromOutside () throws SoundTransformException {
        new ExportAFileFromAFileUsingSoundTransform ().transformFile (input, output, new EightBitsSoundTransformation (25));
    }

    @Test
    public void callPlaySoundFromOutside () throws SoundTransformException {
        new PlaySound ().play (new Sound [0]);
    }
}
