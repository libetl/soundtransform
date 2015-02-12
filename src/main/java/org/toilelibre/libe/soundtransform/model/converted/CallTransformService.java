package org.toilelibre.libe.soundtransform.model.converted;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class CallTransformService extends AbstractLogAware<CallTransformService> {

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

    public CallTransformService () {
    }

    public CallTransformService (final Observer... observers) {
        this.setObservers (observers);
    }

    public Sound [] transformAudioStream (final Sound [] input, final SoundTransformation... sts) throws SoundTransformException {
        Sound [] output = new Sound [input.length];
        int transformNumber = 0;
        for (final SoundTransformation st : sts) {
            for (int i = 0 ; i < input.length ; i++) {
                this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORM_STARTING, transformNumber + 1, sts.length, st.getClass ().getSimpleName (), i + 1, input.length));
                if (st instanceof LogAware) {
                    ((LogAware<?>) st).setObservers (this.observers);
                }
                output [i] = st.transform (output [i] == null ? input [i] : output [i]);
            }
            transformNumber++;
        }
        if (sts.length == 0) {
            output = input;
        }
        this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORMS_DONE));
        return output;

    }
}
