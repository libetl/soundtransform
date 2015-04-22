package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class ApplySoundTransform extends Action {

    public ApplySoundTransform (final Observer... observers) {
        super (observers);
    }

    public <T, U> U [] apply (final T [] input, final SoundTransform<T, U> transform) throws SoundTransformException {
        return this.callTransform.apply (input, transform);
    }
}
