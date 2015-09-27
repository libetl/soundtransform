package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.AbstractFrequencySoundTransform;

public interface FourierTransformHelper<T extends Serializable> {

    Channel reverse (Spectrum<T> spectrum);

    Channel transform (AbstractFrequencySoundTransform<T> st, Channel sound);
}
