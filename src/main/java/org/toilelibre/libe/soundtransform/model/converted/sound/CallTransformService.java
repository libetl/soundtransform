package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface CallTransformService<T> extends LogAware<T> {

    public enum CallTransformServiceEventCode implements EventCode {

        TRANSFORM_STARTING (LogLevel.INFO, "Transform %1d/%2d ( %3s ), channel %4d/%5d"), TRANSFORMS_DONE (LogLevel.INFO, "Transforms done");

        private final String   messageFormat;
        private final LogLevel logLevel;

        CallTransformServiceEventCode (final LogLevel ll, final String mF) {
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
    public abstract Sound[] apply(Sound[] input, SoundTransformation... sts) throws SoundTransformException;

}