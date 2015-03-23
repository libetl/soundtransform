package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

final class DefaultCallTransformService extends AbstractLogAware<DefaultCallTransformService> implements CallTransformService<AbstractLogAware<DefaultCallTransformService>> {


    public DefaultCallTransformService () {
    }
    
    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService#apply(org.toilelibre.libe.soundtransform.model.converted.sound.Sound[], org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation)
     */
    @Override
    public Sound [] apply (final Sound [] input, final SoundTransformation... sts) throws SoundTransformException {
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
