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

    public AudioInputStream (final File f) throws IOException {
        super (new FileInputStream (f));
    }

    public AudioInputStream (final InputStream is) throws IOException {
        super (is);
    }

    private int byteArrayToInt (final byte [] bytes) {
        return bytes [3] << 24 | (bytes [2] & 0xFF) << 16 | (bytes [1] & 0xFF) << 8 | bytes [0] & 0xFF;
    }

    private int byteArrayToShort (final byte [] bytes) {
        return (bytes [1] & 0xFF) << 8 | bytes [0] & 0xFF;
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
        return (short) this.byteArrayToShort (this.shortBuffer);
    }

    void setInfo (final InputStreamInfo info) {
        this.info = info;
    }

}
