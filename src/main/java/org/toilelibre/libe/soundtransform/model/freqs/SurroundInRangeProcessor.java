package org.toilelibre.libe.soundtransform.model.freqs;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface SurroundInRangeProcessor {
    public enum SurroundInRangeProcessorErrorCode implements ErrorCode {

        INVALID_RANGE ("Invalid range, high is lower than or equal to low (low=%1f, high=%1f)");

        private final String messageFormat;

        SurroundInRangeProcessorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    float [] surroundFreqsInRange (float [] freqs, float low, float high) throws SoundTransformException;

}
