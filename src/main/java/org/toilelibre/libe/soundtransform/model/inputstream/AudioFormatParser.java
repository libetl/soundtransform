package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface AudioFormatParser {
    public enum AudioFormatParserErrorCode implements ErrorCode {

        WRONG_TYPE ("incorrect stream type"), READ_ERROR ("Could not parse the format of the stream");

        private final String messageFormat;

        AudioFormatParserErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    Object audioFormatfromStreamInfo (StreamInfo info);

    StreamInfo fromAudioFormat (Object audioFormat1, long l) throws SoundTransformException;

    StreamInfo getStreamInfo (InputStream ais) throws SoundTransformException;
}
