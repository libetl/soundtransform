package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.Arrays;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

/**
 * Finds the loudest frequencies array using the Harmonic Product Spectrum
 * algorithm
 *
 * @param <T>
 *            The kind of object held inside a spectrum.
 */
public class HarmonicProductSpectrumSoundTransform<T extends Serializable> extends AbstractLogAware<HarmonicProductSpectrumSoundTransform<T>> implements PeakFindSoundTransform<T, AbstractLogAware<HarmonicProductSpectrumSoundTransform<T>>> {
    private static final int TWICE = 2;

    static class HarmonicProductSpectrumFrequencySoundTransform<T extends Serializable> extends SimpleFrequencySoundTransform<T> {

        private double                  step;
        private float []                loudestfreqs;
        private boolean                 note;
        private float                   fsLimit;
        private int                     windowLength;
        private int                     soundLength;
        private float                   detectedNoteVolume;

        private final SpectrumHelper<T> spectrumHelper;

        /**
         * Default constructor
         *
         * @param note1
         *            if true, the whole sound will be transformed at once to
         *            know the loudest freq. therefore the array will be of size
         *            1.
         */
        public HarmonicProductSpectrumFrequencySoundTransform (final boolean note1) {
            this (note1, 100, -1);
        }

        /**
         * Constructor not using the whole sound as a musical note
         *
         * @param step1
         *            the iteration step value
         */
        public HarmonicProductSpectrumFrequencySoundTransform (final double step1) {
            this (false, step1, -1);
        }

        /**
         * Constructor not using the whole sound as a musical note
         *
         * @param step1
         *            the iteration step value
         * @param windowLength1
         *            length of the spectrum used during each iteration (the
         *            highest the slowest)
         */
        @SuppressWarnings ("unchecked")
        public HarmonicProductSpectrumFrequencySoundTransform (final boolean note1, final double step1, final int windowLength1) {
            this.spectrumHelper = $.select (SpectrumHelper.class);
            this.step = step1;
            this.note = note1;
            this.windowLength = windowLength1;
        }

        private float bestCandidate (final float [] peaks) {
            int leftEdge = 0;
            while (leftEdge < peaks.length && peaks [leftEdge] <= 30) {
                leftEdge++;
            }
            int rightEdge = leftEdge;
            while (rightEdge < peaks.length && Math.abs ((peaks [rightEdge] - peaks [leftEdge]) * 1.0 / peaks [rightEdge]) * 100.0 < 10) {
                rightEdge++;
            }
            int sum = 0;
            for (int i = leftEdge ; i < rightEdge ; i++) {
                sum += peaks [i];
            }

            return rightEdge == leftEdge ? sum : sum * 1.0f / (rightEdge - leftEdge);
        }

        public float getDetectedNoteVolume () {
            return this.detectedNoteVolume;
        }

        public float [] getLoudestFreqs () {
            return this.loudestfreqs.clone ();
        }

        @Override
        public double getStep (final double defaultValue) {
            return this.step;
        }

        @Override
        public int getWindowLength (final double freqmax) {
            if (this.windowLength != -1) {
                return this.windowLength;
            }
            return (int) Math.pow (2, Math.ceil (Math.log (this.fsLimit) / Math.log (2)));
        }

        @Override
        public Channel initSound (final Channel input) {
            if (this.note) {
                this.step = input.getSamplesLength ();
                this.fsLimit = input.getSamplesLength ();
                this.loudestfreqs = new float [1];
            } else {
                this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
                this.fsLimit = input.getSampleRate ();
            }
            this.soundLength = input.getSamplesLength ();
            return super.initSound (input);
        }

        @Override
        public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

            final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.soundLength / this.step));
            if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.soundLength / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.soundLength / this.step), percent));
            }
            float f0 = 0;

            if (soundLevelInDB > 30 || this.note) {

                final float [] peaks = new float [10];
                for (int i = 1 ; i <= 10 ; i++) {
                    peaks [i - 1] = this.f0 (fs, i);
                }
                Arrays.sort (peaks);
                f0 = this.bestCandidate (peaks);
            }

            if (this.note) {
                this.detectedNoteVolume = soundLevelInDB;
            }
            this.loudestfreqs [(int) (offset / this.step)] = f0;
            return fs;
        }
        
        /**
         * Find the f0 (fundamental frequency) using the Harmonic Product Spectrum
         *
         * @param fs
         *            spectrum at a specific time
         * @param hpsfactor
         *            number of times to multiply the frequencies together
         * @return a fundamental frequency (in Hz)
         */
        public float f0 (final Spectrum<T> fs, final int hpsfactor) {
            final Spectrum<T> productOfMultiples = this.spectrumHelper.productOfMultiples (fs, hpsfactor);
            final int spectrumLength = this.spectrumHelper.getLengthOfSpectrum (fs);
            final int maxIndex = this.spectrumHelper.getMaxIndex (productOfMultiples, 0, spectrumLength / hpsfactor);
            return this.spectrumHelper.freqFromSampleRate (maxIndex, spectrumLength * HarmonicProductSpectrumSoundTransform.TWICE / hpsfactor, fs.getSampleRate ());
        }
    }

    private final HarmonicProductSpectrumFrequencySoundTransform<T> decoratedTransform;

    /**
     * Default constructor
     *
     * @param note1
     *            if true, the whole sound will be transformed at once to know
     *            the loudest freq. therefore the array will be of size 1.
     */
    public HarmonicProductSpectrumSoundTransform (final boolean note1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (note1);
    }

    /**
     * Constructor not using the whole sound as a musical note
     *
     * @param step1
     *            the iteration step value
     */
    public HarmonicProductSpectrumSoundTransform (final double step1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (step1);
    }

    /**
     * Full constructor with every parameter specified
     *
     * @param step1
     *            the iteration step value
     * @param note1
     *            if true, the whole sound will be transformed at once to know
     *            the loudest freq. therefore the array will be of size 1.
     * @param windowLength1
     *            length of the spectrum used during each iteration (the highest
     *            the slowest)
     */
    public HarmonicProductSpectrumSoundTransform (final boolean note1, final double step1, final int windowLength1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (note1, step1, windowLength1);
    }

    @Override
    public float [] transform (final Channel input) throws SoundTransformException {
        this.decoratedTransform.transform (input);
        return this.decoratedTransform.getLoudestFreqs ();
    }

    @Override
    public float getDetectedNoteVolume () {
        return this.decoratedTransform.getDetectedNoteVolume ();
    }

    @Override
    public HarmonicProductSpectrumSoundTransform<T> setObservers (final Observer... observers1) {
        this.decoratedTransform.setObservers (observers1);
        return this;
    }

}
