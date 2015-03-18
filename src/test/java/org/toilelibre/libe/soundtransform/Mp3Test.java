package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class Mp3Test extends SoundTransformTest {

    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        mp3input    = new File (this.classLoader.getResource ("mp3test.mp3").getFile ());

    private final File        output      = new File (new File (this.classLoader.getResource ("mp3test.mp3").getFile ()).getParent () + "/after.wav");

    @Test
    public void testNoOp () throws SoundTransformException {
        FluentClient.start().withFile(this.mp3input).convertIntoSound().apply(new NoOpSoundTransformation()).exportToFile(this.output);

    }
}
