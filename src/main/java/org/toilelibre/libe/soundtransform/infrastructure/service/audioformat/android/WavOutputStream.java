package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class WavOutputStream extends DataOutputStream {

    private static final int INTEGER_NUMBER_OF_BYTES = Integer.SIZE / Byte.SIZE;
    private static final int BYTE_MAX_VALUE          = (1 << Byte.SIZE) - 1;

    public WavOutputStream (final OutputStream stream) {
        super (stream);
    }

    private byte [] intToByteArray (final int n) {
        final byte [] b = new byte [WavOutputStream.INTEGER_NUMBER_OF_BYTES];
        for (int i = 0 ; i < b.length ; i++) {
            b [i] = (byte) (n >> i * Byte.SIZE);
        }
        return b;
    }

    private byte [] shortToByteArray (final int i) {
        return new byte [] { (byte) (i & WavOutputStream.BYTE_MAX_VALUE), (byte) (i >> Byte.SIZE & WavOutputStream.BYTE_MAX_VALUE) };
    }

    public void writeInteger (final int i) throws IOException {
        this.write (this.intToByteArray (i));
    }

    public void writeShortInt (final int i) throws IOException {
        this.write (this.shortToByteArray (i));
    }
}
