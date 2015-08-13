package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class MagnitudeADSRHelperTest {

    @Test
    public void decayWithoutException () throws SoundTransformException {
        new MagnitudeADSRHelper ().findDecay (new double [] { 1, 7, 15, 45, 80, 75, 76, 77, 78, 80 }, 5);
    }

    @Test
    public void sustainWithoutException () throws SoundTransformException {
        new MagnitudeADSRHelper ().findSustain (new double [] { 1, 7, 15, 45, 80, 79, 78, 77, 76, 75 }, 4);
    }

    @Test
    public void releaseWithoutException () throws SoundTransformException {
        new MagnitudeADSRHelper ().findRelease (new double [] { 1, 7, 15, 45, 80, 84, 95, 96, 97, 99 });
    }
}
