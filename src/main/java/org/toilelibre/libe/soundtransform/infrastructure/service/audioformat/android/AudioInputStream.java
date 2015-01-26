package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioInputStream extends DataInputStream implements HasInputStreamInfo {

    private final byte []   intBuffer   = new byte [4];
    private final byte []   shortBuffer = new byte [2];
    private InputStreamInfo info;

    public AudioInputStream (File f) throws IOException {
        super (new FileInputStream (f));
    }
    
    public AudioInputStream (InputStream is) throws IOException {
        super (is);
    }

    private int byteArrayToInt (byte [] b) {
        int i = 0;
        for (int j = 0 ; j < b.length ; j++) {
            i += (b [j]) << (j * 8);
        }
        return i;
    }

    @Override
    public InputStreamInfo getInfo () {
        return this.info;
    }

    String readFourChars () throws IOException {
        this.read (this.intBuffer);
        return new String (this.intBuffer);
    }

    int readInt2 () throws IOException {
        this.read (this.intBuffer);
        return this.byteArrayToInt (this.intBuffer);
    }

    short readShort2 () throws IOException {
        this.read (this.shortBuffer);
        return (short) this.byteArrayToInt (this.shortBuffer);
    }

    void setInfo (InputStreamInfo info) {
        this.info = info;
    }

}
