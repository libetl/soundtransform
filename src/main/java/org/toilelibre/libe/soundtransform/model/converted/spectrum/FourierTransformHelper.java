package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface FourierTransformHelper<T> {

    public Sound reverse (Spectrum<T> spectrum);

    public Sound transform (AbstractFrequencySoundTransformation<T> st, Sound sound);
}
