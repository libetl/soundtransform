package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface AudioFileHelper {

    public enum AudioFileHelperErrorCode implements ErrorCode {

        COULD_NOT_CONVERT ("%1s could not be converted"), COULD_NOT_CREATE_A_TEMP_FILE ("Could not create a temp file"), NO_SOURCE_INPUT_STREAM ("%1s did not provide any source input stream"), NO_DEST_INPUT_STREAM ("%1s did not provide any converted input stream"), WRONG_TYPE (
                "%1s is of wrong type"), AUDIO_FORMAT_COULD_NOT_BE_READ ("Audio format object could not be read"), COULD_NOT_CREATE_AN_OUTPUT_FILE ("Could not create an output file");

        private final String messageFormat;

        AudioFileHelperErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public InputStream getAudioInputStream (File inputFile) throws SoundTransformException;

    public InputStream toStream (byte [] byteArray, Object audioFormat) throws SoundTransformException;

    public void writeInputStream (InputStream ais2, File fDest) throws SoundTransformException;
}
