package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

public abstract class AbstractFrequencySoundTransformation<T> extends AbstractLogAware<AbstractFrequencySoundTransformation<T>> implements SoundTransformation {

    private final FourierTransformHelper<T> fourierTransformHelper;

    public AbstractFrequencySoundTransformation (final FourierTransformHelper<T> helper1) {
        this.fourierTransformHelper = helper1;
    }

    public abstract double getLowThreshold (double defaultValue);

    public abstract int getOffsetFromASimpleLoop (int i, double step);

    public int getWindowLength (final double freqmax) {
        return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
    }

    public abstract Sound initSound (Sound input);

    @Override
    public Sound transform (final Sound sound) {
        return this.fourierTransformHelper.transform (this, sound);
    }

    public abstract Spectrum<T> transformFrequencies (Spectrum<T> fs, int offset, int powOf2NearestLength, int length, float soundLevelInDB);

}
