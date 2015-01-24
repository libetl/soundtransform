package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioInputStream extends FileInputStream implements HasInputStreamInfo {

    private byte []         intBuffer   = new byte [4];
    private byte []         shortBuffer = new byte [2];
    private InputStreamInfo info;

    public AudioInputStream (File file) throws IOException {
        super (file);
    }

    void setInfo (InputStreamInfo info) {
        this.info = info;
    }

    public InputStreamInfo getInfo () {
        return this.info;
    }

    String readFourChars () throws IOException {
        this.read (intBuffer);
        return new String (intBuffer);
    }

    int readShort () throws IOException {
        this.read (shortBuffer);
        return this.byteArrayToInt (shortBuffer);
    }

    int readInt () throws IOException {
        this.read (intBuffer);
        return this.byteArrayToInt (intBuffer);
    }

    private int byteArrayToInt (byte [] b) {
        int i = 0;
        for (int j = 0 ; j < b.length ; j++) {
            i += (b [j]) << j * 8;
        }
        return i;
    }

}
