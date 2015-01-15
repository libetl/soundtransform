package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class TransformAudioInputStreamUsingSoundTransform extends Action {

    public InputStream transformAudioStream (final InputStream ais, final SoundTransformation... sts) throws SoundTransformException {
        return this.transformSound.transformAudioStream (ais, sts);
    }

}
