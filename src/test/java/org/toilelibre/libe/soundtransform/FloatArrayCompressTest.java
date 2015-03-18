package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.freqs.CompressFrequenciesProcessor;

public class FloatArrayCompressTest extends SoundTransformTest {

    @Test
    public void shrink () {
        final float [] array = { 0, 1, 2, 3, 4, 5, 6 };
        org.junit.Assert.assertArrayEquals (new float [] { 0, 2, 4, 6 }, $.select (CompressFrequenciesProcessor.class).compress (array, 2f), 0);
    }

    @Test
    public void stretch () {
        final float [] array = { 0, 1, 2, 3, 4, 5, 6 };
        org.junit.Assert.assertArrayEquals (new float [] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6 }, $.select (CompressFrequenciesProcessor.class).compress (array, 0.5f), 0);
    }
}
