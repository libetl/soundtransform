package org.toilelibre.libe.soundtransform.model.converted;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class CallTransformService implements LogAware<CallTransformService> {

    Observer []    observers    = new Observer [0];

    public CallTransformService (final Observer... observers) {
        this.setObservers (observers);
    }

    @Override
    public void log (final LogEvent event) {
        for (final Observer to : this.observers) {
            to.notify (event);
        }

    }

    private void notifyAll (final String s) {
        this.log (new LogEvent (LogLevel.INFO, s));
    }

    @Override
    public CallTransformService setObservers (final Observer... observers2) {
        this.observers = observers2;
        for (final Observer observer : observers2) {
            this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
        }
        return this;
    }

    public Sound [] transformAudioStream (final Sound [] input, final SoundTransformation... sts) throws SoundTransformException {
        Sound [] output = Arrays.copyOf (input, input.length);
        int transformNumber = 0;
        for (final SoundTransformation st : sts) {
            for (int i = 0; i < input.length; i++) {
                this.notifyAll ("Transform " + (transformNumber + 1) + "/" + sts.length + " (" + st.getClass ().getSimpleName () + "), channel " + (i + 1) + "/" + input.length);
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
        this.notifyAll ("Transforms done");
        return output;

    }
}
