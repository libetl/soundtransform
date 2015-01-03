package org.toilelibre.libe.soundtransform;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.frames.ByteArrayFrameProcessor;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;

public class Pcm2FrameTest {

    @Test
    public void testReversibleData () throws IOException {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [256];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        System.out.println (Arrays.toString (data));
        final TransformInputStreamService ts = new TransformInputStreamService (new PrintlnTransformObserver (true));
        final Sound [] channels = ts.byteArrayToFrames (data, 2, data.length / 4, 2, 44100.0, false, true);

        final byte [] out = new ByteArrayFrameProcessor ().framesToByteArray (channels, 2, false, true);
        System.out.println (Arrays.toString (out));
    }
}
