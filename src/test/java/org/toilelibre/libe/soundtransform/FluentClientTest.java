package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

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

    @Test
    public void readRawInputStream () throws SoundTransformException {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [65536];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        final InputStream is = new ByteArrayInputStream (data);
        InputStreamInfo isi = new InputStreamInfo (1, 32768, 2, 8000, false, true);

        FluentClient.go ().withRawInputStream (is, isi).importToSound ().exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
    }

}
