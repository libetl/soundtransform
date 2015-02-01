package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class ApplySoundTransform extends Action {

    public Sound [] apply (final Sound [] sounds, final SoundTransformation transform) throws SoundTransformException {
        return this.transformSound.apply (sounds, transform);
    }
}
