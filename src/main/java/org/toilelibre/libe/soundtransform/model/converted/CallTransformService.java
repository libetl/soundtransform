package org.toilelibre.libe.soundtransform.model.converted;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class CallTransformService extends AbstractLogAware<CallTransformService> {

    public CallTransformService () {
    }

    public CallTransformService (final Observer... observers) {
        this.setObservers (observers);
    }

    public Sound [] transformAudioStream (final Sound [] input, final SoundTransformation... sts) throws SoundTransformException {
        Sound [] output = Arrays.copyOf (input, input.length);
        int transformNumber = 0;
        for (final SoundTransformation st : sts) {
            for (int i = 0 ; i < input.length ; i++) {
                this.log (new LogEvent (LogLevel.INFO, "Transform " + (transformNumber + 1) + "/" + sts.length + " (" + st.getClass ().getSimpleName () + "), channel " + (i + 1) + "/" + input.length));
                if (st instanceof LogAware) {
                    ((LogAware<?>) st).setObservers (this.observers);
                }
                output [i] = st.transform (output [i]);
            }
            transformNumber++;
        }
        if (sts.length == 0) {
            output = input;
        }
        this.log (new LogEvent (LogLevel.INFO, "Transforms done"));
        return output;

    }
}
