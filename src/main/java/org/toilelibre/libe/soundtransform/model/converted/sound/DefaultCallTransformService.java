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
    @Override
    public <U, V> V [] apply (final U [] input, final SoundTransform<U, V> transform) throws SoundTransformException {
        final Object [] untypedOutput = new Object [input.length];
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

    private <V> V [] typeArrayWithFirstClassValue (final Object [] untypedOutput) throws SoundTransformException {
        if (untypedOutput.length == 0) {
            throw new SoundTransformException (CallTransformServiceErrorCode.NOTHING_IN_INPUT, new NullPointerException ());
        }
        @SuppressWarnings ("unchecked")
        final V [] typedArray = (V []) Array.newInstance (untypedOutput [0].getClass (), untypedOutput.length);
        System.arraycopy (untypedOutput, 0, typedArray, 0, untypedOutput.length);
        return typedArray;
    }
}
