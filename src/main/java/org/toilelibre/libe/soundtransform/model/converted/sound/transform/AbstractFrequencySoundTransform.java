package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

public abstract class AbstractFrequencySoundTransform<T extends Serializable> extends AbstractLogAware<AbstractFrequencySoundTransform<T>> implements SoundTransform<Channel, Channel> {

    private static final double             LOG_2 = Math.log (2);
    private static final int                TWO   = 2;
    private final FourierTransformHelper<T> fourierTransformHelper;

    public AbstractFrequencySoundTransform (final FourierTransformHelper<T> helper1) {
        this.fourierTransformHelper = helper1;
    }

    public abstract int getOffsetFromASimpleLoop (int i, double step);

    public abstract double getStep (double defaultValue);

    public int getWindowLength (final double freqmax) {
        return (int) Math.pow (AbstractFrequencySoundTransform.TWO, Math.ceil (Math.log (freqmax) / AbstractFrequencySoundTransform.LOG_2));
    }

    public abstract Channel initSound (Channel input);

    @Override
    public final Channel transform (final Channel sound) {
        return this.fourierTransformHelper.transform (this, sound);
    }

    public abstract Spectrum<T> transformFrequencies (Spectrum<T> fs, int offset, int powOf2NearestLength, int length, float soundLevelInDB);

}
