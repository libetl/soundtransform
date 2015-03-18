package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class ApplySoundTransform extends Action {

    public ApplySoundTransform(final Observer... observers) {
        super(observers);
    }

    public Sound[] apply(final Sound[] sounds, final SoundTransformation transform) throws SoundTransformException {
        return this.callTransform.apply(sounds, transform);
    }
}
