package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface PeakFindSoundTransformation<T extends Serializable> extends SoundTransformation, LogAware<AbstractLogAware<AbstractFrequencySoundTransformation<T>>> {

    public enum PeakFindSoundTransformationEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "Iteration #%1d/%2d, %3d%%");

        private final String   messageFormat;
        private final LogLevel logLevel;

        PeakFindSoundTransformationEventCode (final LogLevel ll, final String mF) {
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
    
    float [] getLoudestFreqs ();

    float getDetectedNoteVolume ();

    List<float[]> getAllLoudestFreqs();
}
