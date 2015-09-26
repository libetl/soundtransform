package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToByteArrayHelper;

final class WriteInputStreamToByteArray implements InputStreamToByteArrayHelper {

    public enum WriteInputStreamToByteArrayErrorCode implements ErrorCode {

        ERROR_WHILE_READING_STREAM ("System error while reading stream");

        private final String messageFormat;

        WriteInputStreamToByteArrayErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public WriteInputStreamToByteArray () {

    }

    @Override
    public byte [] convertToByteArray (final InputStream inputStream) throws SoundTransformException {
        final DataInputStream dataInputStream = new DataInputStream (inputStream);
        byte [] byteArray;
        try {
            byteArray = new byte [dataInputStream.available ()];
            dataInputStream.read (byteArray);
            return byteArray;
        } catch (final IOException e) {
            throw new SoundTransformException (WriteInputStreamToByteArrayErrorCode.ERROR_WHILE_READING_STREAM, e, inputStream.toString ());
        }

    }

}
