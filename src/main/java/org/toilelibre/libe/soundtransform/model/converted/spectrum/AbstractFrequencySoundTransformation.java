package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

public abstract class AbstractFrequencySoundTransformation<T extends Serializable> extends AbstractLogAware<AbstractFrequencySoundTransformation<T>> implements SoundTransformation {

    private static final double LOG_2 = Math.log(2);
    private static final int TWO = 2;
    private final FourierTransformHelper<T> fourierTransformHelper;

    public AbstractFrequencySoundTransformation(final FourierTransformHelper<T> helper1) {
        this.fourierTransformHelper = helper1;
    }

    public abstract int getOffsetFromASimpleLoop(int i, double step);

    public abstract double getStep(double defaultValue);

    public int getWindowLength(final double freqmax) {
        return (int) Math.pow(AbstractFrequencySoundTransformation.TWO, Math.ceil(Math.log(freqmax) / AbstractFrequencySoundTransformation.LOG_2));
    }

    public abstract Sound initSound(Sound input);

    @Override
    public Sound transform(final Sound sound) {
        return this.fourierTransformHelper.transform(this, sound);
    }

    public abstract Spectrum<T> transformFrequencies(Spectrum<T> fs, int offset, int powOf2NearestLength, int length, float soundLevelInDB);

}
