package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

final class AudioInputStream extends DataInputStream implements HasStreamInfo {
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

    static final String      DEFAULT_CHARSET_NAME = "UTF-8";
    private static final int INTEGER_BYTE_NUMBER  = Integer.SIZE / Byte.SIZE;
    private static final int SHORT_BYTE_NUMBER    = Short.SIZE / Byte.SIZE;
    private static final int BYTE_MAX_VALUE       = (1 << Byte.SIZE) - 1;
    private static final int FOURTH_INDEX         = 3;
    private static final int FOURTH_BYTE          = Byte.SIZE * AudioInputStream.FOURTH_INDEX;
    private static final int THIRD_INDEX          = 2;
    private static final int THIRD_BYTE           = Byte.SIZE * AudioInputStream.THIRD_INDEX;
    private static final int SECOND_INDEX         = 1;
    private static final int SECOND_BYTE          = Byte.SIZE * AudioInputStream.SECOND_INDEX;
    private static final int FIRST_INDEX          = 0;

    private final byte []    intBuffer            = new byte [AudioInputStream.INTEGER_BYTE_NUMBER];
    private final byte []    shortBuffer          = new byte [AudioInputStream.SHORT_BYTE_NUMBER];
    private StreamInfo       info;

    public AudioInputStream (final File f) throws IOException {
        super (new FileInputStream (f));
    }

    public AudioInputStream (final InputStream is) throws IOException {
        super (is);
    }

    private int byteArrayToInt (final byte [] bytes) {
        return bytes [AudioInputStream.FOURTH_INDEX] << AudioInputStream.FOURTH_BYTE | (bytes [AudioInputStream.THIRD_INDEX] & AudioInputStream.BYTE_MAX_VALUE) << AudioInputStream.THIRD_BYTE | (bytes [AudioInputStream.SECOND_INDEX] & AudioInputStream.BYTE_MAX_VALUE) << AudioInputStream.SECOND_BYTE
                | bytes [AudioInputStream.FIRST_INDEX] & AudioInputStream.BYTE_MAX_VALUE;
    }

    private int byteArrayToShort (final byte [] bytes) {
        return (bytes [AudioInputStream.SECOND_INDEX] & AudioInputStream.BYTE_MAX_VALUE) << AudioInputStream.SECOND_BYTE | bytes [AudioInputStream.FIRST_INDEX] & AudioInputStream.BYTE_MAX_VALUE;
    }

    @Override
    public StreamInfo getInfo () {
        return this.info;
    }

    String readFourChars () throws IOException {
        final int i = this.read (this.intBuffer);
        if (i != AudioInputStream.INTEGER_BYTE_NUMBER) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), AudioInputStream.INTEGER_BYTE_NUMBER, i);
        }
        return new String (this.intBuffer, AudioInputStream.DEFAULT_CHARSET_NAME);
    }

    int readInt2 () throws IOException {
        final int i = this.read (this.intBuffer);
        if (i != AudioInputStream.INTEGER_BYTE_NUMBER) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), AudioInputStream.INTEGER_BYTE_NUMBER, i);
        }
        return this.byteArrayToInt (this.intBuffer);
    }

    short readShort2 () throws IOException {
        final int i = this.read (this.shortBuffer);
        if (i != AudioInputStream.SHORT_BYTE_NUMBER) {
            throw new SoundTransformRuntimeException (AudioInputStreamErrorCode.WRONG_FORMAT_READ_VALUE, new IllegalArgumentException (), AudioInputStream.SHORT_BYTE_NUMBER, i);
        }
        return (short) this.byteArrayToShort (this.shortBuffer);
    }

    void setInfo (final StreamInfo info1) {
        this.info = info1;
    }

}
