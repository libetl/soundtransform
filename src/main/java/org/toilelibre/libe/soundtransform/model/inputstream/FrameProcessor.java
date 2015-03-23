package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface FrameProcessor<T> extends LogAware<T> {

    public enum FrameProcessorErrorCode implements ErrorCode {
        COULD_NOT_READ_STREAM ("Could not read stream"), COULD_NOT_CLOSE_STREAM ("Could not close stream");

        private final String messageFormat;

        FrameProcessorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum FrameProcessorEventCode implements EventCode {
        READ_START (LogLevel.INFO, "Starting to read the input stream"), BYTEARRAY_TO_FRAME_CONVERSION (LogLevel.VERBOSE, "Converting a byte array into a sound frame (%1d/%2d, %3d%%)"), READ_END (LogLevel.INFO, "Finished reading the input stream"), SOUND_INIT (LogLevel.INFO,
                "Converted sound allocation in memory"), READ_FRAME_SIZE (LogLevel.PARANOIAC, "Read frame size : %1d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        FrameProcessorEventCode (final LogLevel ll, final String mF) {
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

    public abstract byte [] framesToByteArray (Sound [] channels, StreamInfo streamInfo);

    public abstract Sound [] fromInputStream (InputStream ais, StreamInfo isInfo) throws SoundTransformException;

}