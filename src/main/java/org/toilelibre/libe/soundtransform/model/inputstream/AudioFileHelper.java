package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface AudioFileHelper {

    public enum AudioFileHelperErrorCode implements ErrorCode {

        COULD_NOT_CONVERT ("%1s could not be converted"), NO_SOURCE_INPUT_STREAM ("%1s did not provide any source input stream"), NO_DEST_INPUT_STREAM ("%1s did not provide any converted input stream"), WRONG_TYPE ("%1s is of wrong type"), AUDIO_FORMAT_COULD_NOT_BE_READ (
                "Audio format object could not be read"), COULD_NOT_CREATE_AN_OUTPUT_FILE ("Could not create an output file"), PROBLEM_IN_THE_LIBRARY ("Internal error in the library, could not convert a sound file"), CUSTOM_CONVERSION_FAILED ("The conversion from a %1s file failed"), COULD_NOT_CONVERT_IS (
                        "Input Stream could not be read");

        private final String messageFormat;

        AudioFileHelperErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum AudioFileHelperEventCode implements EventCode {
        COULD_NOT_CLOSE (LogLevel.ERROR, "Could not close the stream"), CONVERTING_FIRST (LogLevel.INFO, "Converting first into %1s");

        private final String   messageFormat;
        private final LogLevel logLevel;

        AudioFileHelperEventCode (final LogLevel ll, final String mF) {
            this.logLevel = ll;
            this.messageFormat = mF;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    InputStream getAudioInputStream (File inputFile) throws SoundTransformException;

    InputStream getAudioInputStream (InputStream rawInputStream) throws SoundTransformException;

    InputStream toStream (byte [] byteArray, Object audioFormatfromStreamInfo) throws SoundTransformException;

    void writeInputStream (InputStream ais2, File fDest) throws SoundTransformException;
}
