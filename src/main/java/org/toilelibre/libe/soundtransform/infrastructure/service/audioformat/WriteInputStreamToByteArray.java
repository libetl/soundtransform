package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToByteArrayHelper;

class WriteInputStreamToByteArray implements InputStreamToByteArrayHelper {

    private static final int ARBITRARY_ARRAY_LENGTH = 16384;
    
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

    public byte [] convertToByteArray (final InputStream is) throws SoundTransformException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream ();

        int nRead;
        final byte [] data = new byte [WriteInputStreamToByteArray.ARBITRARY_ARRAY_LENGTH];
        try {
            while ((nRead = is.read (data, 0, data.length)) != -1) {
                buffer.write (data, 0, nRead);
            }

            buffer.flush ();
        }catch (IOException exception){
            throw new SoundTransformException(WriteInputStreamToByteArrayErrorCode.ERROR_WHILE_READING_STREAM, exception);
        }

        return buffer.toByteArray ();

    }

}
