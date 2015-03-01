package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class Mp3Test extends SoundTransformTest {

    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        mp3input    = new File (this.classLoader.getResource ("mp3test.mp3").getFile ());

    private final File        output      = new File (new File (this.classLoader.getResource ("mp3test.mp3").getFile ()).getParent () + "/after.wav");

    @Test
    public void testNoOp () throws SoundTransformException {
        $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformFile (this.mp3input, this.output, new NoOpSoundTransformation ());

    }
}
