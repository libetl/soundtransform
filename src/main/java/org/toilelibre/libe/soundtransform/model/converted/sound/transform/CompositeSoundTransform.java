package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class CompositeSoundTransform<I, T, O> implements SoundTransform<I, O> {

    private SoundTransform<I, T> soundTransform1;
    private SoundTransform<T, O> soundTransform2;
    
    public CompositeSoundTransform (SoundTransform<I, T> soundTransform11, SoundTransform<T, O> soundTransform21) {
        this.soundTransform1 = soundTransform11;
        this.soundTransform2 = soundTransform21;
    }
    
    @Override
    public O transform (I input) throws SoundTransformException {
        return this.soundTransform2.transform (this.soundTransform1.transform (input));
    }

}
