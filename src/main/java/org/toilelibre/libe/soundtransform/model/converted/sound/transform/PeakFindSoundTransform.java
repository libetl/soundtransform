package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface PeakFindSoundTransform<T extends Serializable> extends SoundTransform<Sound, Sound>, LogAware<AbstractLogAware<AbstractFrequencySoundTransform<T>>> {

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

    float [] getLoudestFreqs ();

    float getDetectedNoteVolume ();

    List<float []> getAllLoudestFreqs ();
}
