package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;

public class LoudestFreqsTest {

    @Test
    public void adjustFreqs() {
        final float[] array = { 225.32f, 799.2f, 146.11f, 332.74f, 55f, 139f, 1010f };
        final float[] array2 = { 220f, 783.991f, 146.832f, 329.628f, 55f, 146.832f, 987.767f };
        final float[] result = FluentClient.start().withFreqs(array).adjust().stopWithFreqs();
        org.junit.Assert.assertArrayEquals(result, array2, 0);
    }

    @Test
    public void changeOctaveDown() {
        final float[] array = { 1, 2, 3, -2, -6, 10, 12 };
        final float[] array2 = { 0.5f, 1, 1.5f, -1, -3, 5, 6 };
        final float[] result = FluentClient.start().withFreqs(array).octaveDown().stopWithFreqs();
        org.junit.Assert.assertArrayEquals(result, array2, 0);
    }

    @Test
    public void changeOctaveUp() {
        final float[] array = { 1, 2, 3, -2, -6, 10, 12 };
        final float[] array2 = { 2, 4, 6, -4, -12, 20, 24 };
        final float[] result = FluentClient.start().withFreqs(array).octaveUp().stopWithFreqs();
        org.junit.Assert.assertArrayEquals(result, array2, 0);
    }

    @Test
    public void operationShouldBeReversibleEvenWithALotOfOctaveDown() {
        final float[] array = { 1, 2, 3, -2, -6, 10, 12 };
        final float[] result = FluentClient.start().withFreqs(array).octaveDown().octaveDown().octaveDown().octaveDown().octaveDown().octaveUp().octaveUp().octaveUp().octaveUp().octaveUp().stopWithFreqs();
        org.junit.Assert.assertArrayEquals(result, array, 0);
    }
}
