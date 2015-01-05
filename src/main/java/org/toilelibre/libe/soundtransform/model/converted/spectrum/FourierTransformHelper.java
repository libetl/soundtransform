package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface FourierTransformHelper {

    public Sound transform (AbstractFrequencySoundTransformation st, Sound sound);
}
