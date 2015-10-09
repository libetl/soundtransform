package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.logging.EventCode;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

public interface PeakFindSoundTransform<T extends Serializable, U extends LogAware<U>> extends SoundTransform<Channel, float []>, LogAware<U> {

    public enum PeakFindSoundTransformEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "Iteration #%1d/%2d, %3d%%");

        private final String   messageFormat;
        private final LogLevel logLevel;

        PeakFindSoundTransformEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
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

    float getDetectedNoteVolume ();
}
