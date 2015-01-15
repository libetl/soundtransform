package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SlowdownSoundTest {

    @Test
    public void testSlowdown () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ()
                .getContextClassLoader ();
        final File input = new File (classLoader.getResource (
                "notes/g-piano3.wav").getFile ());
        final File output = new File (new File (classLoader.getResource (
                "before.wav").getFile ()).getParent ()
                + "/after.wav");

        $.create (TransformSoundService.class, new PrintlnTransformObserver ())
                .transformFile (input, output,
                        $.create (SlowdownSoundTransformation.class, 200, 2.5f));

    }
}
