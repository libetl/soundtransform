package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class ConvertFromAudioInputStreamAndApplySoundTransform extends Action {

    public Sound [] convertAndApply (final InputStream ais, final SoundTransformation transform) throws SoundTransformException {
        return this.transformSound.convertAndApply (ais, transform);
    }

}
