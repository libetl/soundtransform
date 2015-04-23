package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.lang.reflect.Array;

import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

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
        Object [] untypedOutput = new Object [input.length];
        for (int i = 0 ; i < input.length ; i++) {
            this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORM_STARTING, transform.getClass ().getSimpleName (), i + 1, input.length));
            if (transform instanceof LogAware) {
                ((LogAware<?>) transform).setObservers (this.observers);
            }
            untypedOutput [i] = transform.transform (input [i]);
        }
        this.log (new LogEvent (CallTransformServiceEventCode.TRANSFORMS_DONE));
        return this.typeArrayWithFirstClassValue (untypedOutput);
    }

    private <V> V [] typeArrayWithFirstClassValue (Object [] untypedOutput) {
        if (untypedOutput.length == 0){
            return null;
        }
        @SuppressWarnings ("unchecked")
        V[] typedArray = (V []) Array.newInstance ((Class<V []>) untypedOutput [0].getClass (), untypedOutput.length);
        System.arraycopy (untypedOutput, 0, typedArray, 0, untypedOutput.length);
        return typedArray;
    }
}
