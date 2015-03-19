package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SlowdownTest extends SoundTransformTest {

    @Test
    public void test () throws SoundTransformException {
        final long [] testarray = new long [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        final Sound testsound = new Sound (testarray, new FormatInfo (1, testarray.length), 1);
        final SlowdownSoundTransformation est = new SlowdownSoundTransformation (2, 2, 4);
        final Sound resultsound = est.transform (testsound);
        new Slf4jObserver ().notify (Arrays.toString (resultsound.getSamples ()));
    }
}
