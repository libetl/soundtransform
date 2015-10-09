package org.toilelibre.libe.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

public class Pcm2FrameTest extends SoundTransformTest {

    @Test
    public void testReversibleData () throws SoundTransformException {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [256];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        final InputStreamToSoundService<?> ts = (InputStreamToSoundService<?>) $.select (InputStreamToSoundService.class).setObservers (new Slf4jObserver (LogLevel.WARN));
        final InputStream bais = new ByteArrayInputStream (data);
        final StreamInfo streamInfo = new StreamInfo (2, data.length / 4, 2, 44100.0f, false, true, null);
        final Sound sound = ts.fromInputStream (bais, streamInfo);

        final byte [] out = $.select (FrameProcessor.class).framesToByteArray (sound.getChannels (), streamInfo);
        Assert.assertArrayEquals (data, out);
    }
}
