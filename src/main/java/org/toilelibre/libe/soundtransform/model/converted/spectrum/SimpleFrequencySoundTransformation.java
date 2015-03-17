package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

/**
 * Simple proxy to avoid useless parameters in the overriden method
 *
 * @author lionel
 *
 */
public class SimpleFrequencySoundTransformation<T extends Serializable> extends AbstractFrequencySoundTransformation<T> {

    @SuppressWarnings("unchecked")
    public SimpleFrequencySoundTransformation() {
        super($.select(FourierTransformHelper.class));
    }

    @Override
    public int getOffsetFromASimpleLoop(final int i, final double step) {
        return 0;
    }

    @Override
    public double getStep(final double defaultValue) {
        return defaultValue;
    }

    @Override
    public Sound initSound(final Sound input) {
        final long[] newdata = new long[input.getSamplesLength()];
        return new Sound(newdata, input.getFormatInfo(), input.getChannelNum());
    }

    public Spectrum<T> transformFrequencies(final Spectrum<T> fs) {
        return fs;
    }

    public Spectrum<T> transformFrequencies(final Spectrum<T> fs, final int offset) {
        return this.transformFrequencies(fs);
    }

    public Spectrum<T> transformFrequencies(final Spectrum<T> fs, final int offset, final int powOf2NearestLength) {
        return this.transformFrequencies(fs, offset);
    }

    public Spectrum<T> transformFrequencies(final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length) {
        return this.transformFrequencies(fs, offset, powOf2NearestLength);
    }

    @Override
    public Spectrum<T> transformFrequencies(final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevel) {
        return this.transformFrequencies(fs, offset, powOf2NearestLength, length);
    }
}
