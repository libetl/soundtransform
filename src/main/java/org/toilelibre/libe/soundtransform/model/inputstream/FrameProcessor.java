package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FrameProcessor {

    public enum FrameProcessorErrorCode implements ErrorCode {
        WRONG_TYPE ("incorrect stream type");

        private final String    messageFormat;

        FrameProcessorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public abstract void byteArrayToFrame (byte [] frame, Sound [] sound, int position, boolean bigEndian, boolean pcmSigned, long neutral);

    public abstract byte [] framesToByteArray (Sound [] channels, int sampleSize, boolean bigEndian, boolean pcmSigned);

    public abstract Sound [] fromInputStream (InputStream ais, InputStreamInfo isInfo) throws SoundTransformException;

}