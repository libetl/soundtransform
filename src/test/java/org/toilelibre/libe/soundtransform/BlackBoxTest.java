package org.toilelibre.libe.soundtransform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetSoundInfo;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class BlackBoxTest extends SoundTransformTest {
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
        new ExportAFile ().transformFile (this.input, this.output, new EightBitsSoundTransformation (25));
    }

    @Test
    public void getSoundInfo () throws SoundTransformException, IOException {
        new Slf4jObserver ().notify (new GetSoundInfo ().getSoundInfo (new BufferedInputStream (new FileInputStream (this.gPiano3))).toString ());
    }
}
