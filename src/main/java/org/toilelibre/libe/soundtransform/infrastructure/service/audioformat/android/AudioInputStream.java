package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioInputStream extends FileInputStream implements HasInputStreamInfo {

    private final byte []   intBuffer   = new byte [4];
    private final byte []   shortBuffer = new byte [2];
    private InputStreamInfo info;

    public AudioInputStream (File file) throws IOException {
        super (file);
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

    int readInt () throws IOException {
        this.read (this.intBuffer);
        return this.byteArrayToInt (this.intBuffer);
    }

    int readShort () throws IOException {
        this.read (this.shortBuffer);
        return this.byteArrayToInt (this.shortBuffer);
    }

    void setInfo (InputStreamInfo info) {
        this.info = info;
    }

}
