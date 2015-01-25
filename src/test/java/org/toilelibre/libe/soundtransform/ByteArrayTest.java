package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;

public class ByteArrayTest {

    byte [] array1 = { 42, -127, 23, 0 };

    private int byteArrayToInt (byte [] b) {
        int i = 0;
        for (int j = 0 ; j < b.length ; j++) {
            i += (b [j]) << (j * 8);
        }
        return i;
    }

    private byte [] intToByteArray (int n) {
        final byte [] b = new byte [4];
        int k = n;
        for (int i = 0 ; i < b.length ; i++) {
            b [i] = (byte) (k % 256);
            k >>= 8;
        }
        return b;
    }

    @Test
    public void test1 () {
        int i = this.byteArrayToInt (this.array1);
        i -= 320;
        final byte [] array2 = this.intToByteArray (i);
        System.out.println (Arrays.toString (this.array1));
        System.out.println (i);
        System.out.println (Arrays.toString (array2));
    }
}
