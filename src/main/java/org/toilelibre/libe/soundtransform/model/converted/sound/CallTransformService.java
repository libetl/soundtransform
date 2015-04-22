package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface CallTransformService<T> extends LogAware<T> {

    public enum CallTransformServiceEventCode implements EventCode {

        TRANSFORM_STARTING (LogLevel.INFO, "Transform %3s, channel %4d/%5d"), TRANSFORMS_DONE (LogLevel.INFO, "Transforms done");

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

    public abstract <U, V> V [] apply (U [] input, SoundTransform<U, V> transform) throws SoundTransformException;

}