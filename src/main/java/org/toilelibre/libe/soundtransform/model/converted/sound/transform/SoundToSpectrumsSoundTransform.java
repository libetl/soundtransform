package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Transforms a sound into a list of spectrums. Useful to display a visualizer.
 *
 */
public class SoundToSpectrumsSoundTransform implements SoundTransform<Sound, Spectrum<Serializable> []> {
    static class SoundToSpectrumsFrequencySoundTransform extends SimpleFrequencySoundTransform<Serializable> {

        private static final int          TWO = 2;
        private int                       step;
        private Spectrum<Serializable> [] spectrums;
        private int                       index;

        /**
         * Default constructor
         */
        public SoundToSpectrumsFrequencySoundTransform () {
            super ();
        }

        @SuppressWarnings ("unchecked")
        private Spectrum<Serializable> [] generateSpectrumArray (final int spectrumsSize) {
            return new Spectrum [spectrumsSize];
        }

        public Spectrum<Serializable> [] getSpectrums () {
            return this.spectrums;
        }

        @Override
        public double getStep (final double defaultValue) {
            return this.step;
        }

        @Override
        public Sound initSound (final Sound input) {
            this.index = 0;
            int roundedSize = SoundToSpectrumsFrequencySoundTransform.TWO;
            while (input.getSampleRate () > roundedSize) {
                roundedSize *= SoundToSpectrumsFrequencySoundTransform.TWO;
            }
            this.step = roundedSize;
            final int spectrumsSize = (int) Math.ceil (input.getSamplesLength () * 1.0 / roundedSize);
            this.spectrums = this.generateSpectrumArray (spectrumsSize);
            return super.initSound (input);
        }

        @Override
        public Spectrum<Serializable> transformFrequencies (final Spectrum<Serializable> fs) {
            this.spectrums [this.index++] = fs;
            return fs;
        }
    }

    @Override
    public Spectrum<Serializable> [] transform (final Sound input) throws SoundTransformException {
        final SoundToSpectrumsFrequencySoundTransform soundTransform = new SoundToSpectrumsFrequencySoundTransform ();
        soundTransform.transform (input);
        return soundTransform.getSpectrums ();
    }
}
