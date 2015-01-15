package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public abstract class AbstractFrequencySoundTransformation implements SoundTransformation, LogAware<AbstractFrequencySoundTransformation> {

    private Observer []                  observers;

    private final FourierTransformHelper fourierTransformHelper;

    public AbstractFrequencySoundTransformation (FourierTransformHelper helper1) {
        this.fourierTransformHelper = helper1;
    }

    public abstract double getLowThreshold (double defaultValue);

    public abstract int getOffsetFromASimpleLoop (int i, double step);

    public int getWindowLength (final double freqmax) {
        return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
    }

    public abstract Sound initSound (Sound input);

    @Override
    public void log (final LogEvent logEvent) {
        if (this.observers == null) {
            return;
        }
        for (final Observer transformObserver : this.observers) {
            transformObserver.notify (logEvent);
        }
    }

    @Override
    public AbstractFrequencySoundTransformation setObservers (final Observer... observers1) {
        this.observers = observers1;
        return this;
    }

    @Override
    public Sound transform (final Sound sound) {
        final Sound output = this.fourierTransformHelper.transform (this, sound);
        return output;
    }

    public abstract Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length, float soundLevelInDB);

}
