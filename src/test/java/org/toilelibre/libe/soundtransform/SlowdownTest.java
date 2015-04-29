package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SlowdownTest extends SoundTransformTest {

    @Test
    public void test () throws SoundTransformException {
        final long [] testarray = new long [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        final Channel testsound = new Channel (testarray, new FormatInfo (1, testarray.length), 1);
        final SlowdownSoundTransform est = new SlowdownSoundTransform (2, 2, 4);
        final Channel resultsound = est.transform (testsound);
        new Slf4jObserver ().notify (Arrays.toString (resultsound.getSamples ()));
    }
}
