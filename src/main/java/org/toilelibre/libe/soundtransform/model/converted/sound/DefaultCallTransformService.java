package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

import se.jbee.inject.Array;

final class DefaultCallTransformService extends AbstractLogAware<DefaultCallTransformService> implements CallTransformService<AbstractLogAware<DefaultCallTransformService>> {

    public DefaultCallTransformService () {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService
     * #apply(org.toilelibre.libe.soundtransform.model.converted.sound.Sound[],
     * org.toilelibre.libe.soundtransform.model.converted.sound.transform.
     * SoundTransform)
     */
    public <U, V> V [] apply (U [] input, SoundTransform<U, V> transform) throws SoundTransformException {
        V [] output = (V[]) Array.newArrayInstance(Object[].class, input.length);
        for (int i = 0 ; i < input.length ; i++) {
            this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORM_STARTING, transform.getClass ().getSimpleName (), i + 1, input.length));
            if (transform instanceof LogAware) {
                ((LogAware<?>) transform).setObservers (this.observers);
            }
            output [i] = transform.transform (input [i]);
        }
        this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORMS_DONE));
        return output;
    }
}
