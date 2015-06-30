package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

/**
 * Simple proxy to avoid useless parameters in the overriden method. It is made
 * to be subclassed by your own soundtransform class
 *
 *
 */
public class SimpleFrequencySoundTransform<T extends Serializable> extends AbstractFrequencySoundTransform<T> {

    @SuppressWarnings ("unchecked")
    /**
     * Default constructor
     */
    public SimpleFrequencySoundTransform () {
        super ($.select (FourierTransformHelper.class));
    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return 0;
    }

    @Override
    public double getStep (final double defaultValue) {
        return defaultValue;
    }

    @Override
    public AbstractWindowSoundTransform getWindowTransform () {
        return new NoOpWindowSoundTransform ();
    }
    
    @Override
    public Channel initSound (final Channel input) {
        final long [] newdata = new long [input.getSamplesLength ()];
        return new Channel (newdata, input.getFormatInfo (), input.getChannelNum ());
    }

    public Spectrum<T> transformFrequencies (final Spectrum<T> fs) {
        return fs;
    }

    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset) {
        return this.transformFrequencies (fs);
    }

    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength) {
        return this.transformFrequencies (fs, offset);
    }

    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length) {
        return this.transformFrequencies (fs, offset, powOf2NearestLength);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevel) {
        return this.transformFrequencies (fs, offset, powOf2NearestLength, length);
    }
    
    public void transformFrequencies (final double [] [] spectrumAsDoubles, final float sampleRate) {
    }

    public void transformFrequencies (final double [] [] spectrumAsDoubles, final float sampleRate, final int offset) {
        this.transformFrequencies (spectrumAsDoubles, sampleRate);
    }

    public void transformFrequencies (final double [] [] spectrumAsDoubles, final float sampleRate, final int offset, final int powOf2NearestLength) {
        this.transformFrequencies (spectrumAsDoubles, sampleRate, offset);
    }

    public void transformFrequencies (final double [] [] spectrumAsDoubles, final float sampleRate, final int offset, final int powOf2NearestLength, final int length) {
        this.transformFrequencies (spectrumAsDoubles, sampleRate, offset, powOf2NearestLength);
    }

    @Override
    public void transformFrequencies (final double [] [] spectrumAsDoubles, final float sampleRate, final int offset, final int powOf2NearestLength, final int length, final float soundLevel) {
        this.transformFrequencies (spectrumAsDoubles, sampleRate, offset, powOf2NearestLength, length);
    }

    @Override
    public boolean isReverseNecessary () {
        return true;
    }

    @Override
    public boolean rawSpectrumPrefered () {
        return false;
    }
}
