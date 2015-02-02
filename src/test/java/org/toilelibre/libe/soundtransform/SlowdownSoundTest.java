package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SlowdownSoundTest extends SoundTransformTest {

    @Test
    public void testSlowdown () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (TransformSoundService.class, new Slf4jObserver ()).transformFile (input, output, $.create (SlowdownSoundTransformation.class, 10, 2.5f, 512));

    }
}
