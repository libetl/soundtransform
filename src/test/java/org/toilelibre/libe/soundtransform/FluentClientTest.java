package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class FluentClientTest extends SoundTransformTest {

    @Test
    public void backAndForth () throws SoundTransformException {
        FluentClient.go ().withClasspathResource ("before.wav").convertIntoSound ().apply (new NoOpSoundTransformation ()).exportToClasspathResource ("before.wav").convertIntoSound ();
    }

    @Test
    public void simpleLifeCycle () throws SoundTransformException {
        FluentClient.go ().withClasspathResource ("before.wav").convertIntoSound ().apply (new EightBitsSoundTransformation (25)).exportToClasspathResource ("after.wav");
    }
    

    @Test
    public void twoTimesInOneInstruction () throws SoundTransformException {
        FluentClient.go ().withClasspathResource ("before.wav").convertIntoSound ().andAfterGo ().withClasspathResource ("before.wav").convertIntoSound ();
    }

}
