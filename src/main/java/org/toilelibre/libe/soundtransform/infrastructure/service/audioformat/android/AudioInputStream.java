package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioInputStream extends DataInputStream implements HasInputStreamInfo {
    public enum AudioInputStreamErrorCode implements ErrorCode {

        WRONG_FORMAT_READ_VALUE ("Read value has an invalid format (expected : %1d bytes, got : %2d bytes)");

        private final String messageFormat;

        AudioInputStreamErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

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
        final int i = this.read (this.intBuffer);
        if (i != 4) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), 4, i);
        }
        return new String (this.intBuffer, AndroidWavHelper.DEFAULT_CHARSET_NAME);
    }

    int readInt2 () throws IOException {
        final int i = this.read (this.intBuffer);
        if (i != 4) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), 4, i);
        }
        return this.byteArrayToInt (this.intBuffer);
    }

    short readShort2 () throws IOException {
        final int i = this.read (this.shortBuffer);
        if (i != 4) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), 2, i);
        }
        return (short) this.byteArrayToShort (this.shortBuffer);
    }

    void setInfo (final InputStreamInfo info) {
        this.info = info;
    }

}
