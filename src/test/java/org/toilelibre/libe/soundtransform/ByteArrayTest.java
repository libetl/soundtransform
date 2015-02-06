package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;

public class ByteArrayTest extends SoundTransformTest {

    byte [] array1 = { 42, -127, 23, 0 };

    private int byteArrayToInt (final byte [] bytes) {
        return bytes [3] << 24 | (bytes [2] & 0xFF) << 16 | (bytes [1] & 0xFF) << 8 | bytes [0] & 0xFF;
    }

    private byte [] intToByteArray (final int n) {
        final byte [] b = new byte [4];
        for (int i = 0 ; i < b.length ; i++) {
            b [i] = (byte) (n >> i * 8);
        }
        return b;
    }

    @Test
    public void test1 () {
        int i = this.byteArrayToInt (this.array1);
        i -= 320;
        final byte [] array2 = this.intToByteArray (i);
        new Slf4jObserver ().notify (Arrays.toString (this.array1));
        new Slf4jObserver ().notify ("" + i);
        new Slf4jObserver ().notify (Arrays.toString (array2));
    }

    @Test
    public void testEquals () {
        final int i = this.byteArrayToInt (this.array1);
        final byte [] array2 = this.intToByteArray (i);
        new Slf4jObserver ().notify (Arrays.toString (this.array1));
        new Slf4jObserver ().notify ("" + i);
        new Slf4jObserver ().notify (Arrays.toString (array2));
    }
}
