package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavOutputStream extends FileOutputStream {

    public WavOutputStream (File file) throws FileNotFoundException {
        super (file);
    }

    private byte [] intToByteArray (int n) {
        final byte [] b = new byte [4];
        for (int i = 0 ; i < b.length ; i++) {
            b [i] = (byte) (n >> i * 8);
        }
        return b;
    }

    private byte [] shortToByteArray (int i) {
        return new byte [] { (byte) (i & 0xff), (byte) ((i >> 8) & 0xff) };
    }

    public void writeInt (int i) throws IOException {
        this.write (this.intToByteArray (i));
    }

    public void writeShortInt (int i) throws IOException {
        this.write (this.shortToByteArray (i));
    }
}
