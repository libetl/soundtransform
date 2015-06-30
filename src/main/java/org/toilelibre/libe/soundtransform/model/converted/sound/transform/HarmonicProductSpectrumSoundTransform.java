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
        private static final float PART_OF_THE_SPECTRUM_TO_READ = 0.1f; //2000Hz if the spectrum is 20000Hz long

        private double                  step;
        private float []                loudestfreqs;
        private boolean                 note;
        private float                   fsLimit;
        private int                     windowLength;
        private int                     soundLength;
        private float                   detectedNoteVolume;

        private final SpectrumHelper<T> spectrumHelper;

        private final boolean           useRawData;

        /**
         * Default constructor
         *
         * @param note1
         *            if true, the whole sound will be transformed at once to
         *            know the loudest freq. therefore the array will be of size
         *            1.
         * @param useRawData1
         *            use double array of arrays instead of spectrums
         */
        public HarmonicProductSpectrumFrequencySoundTransform (final boolean note1, final boolean useRawData1) {
            this (note1, 100, -1, useRawData1);
        }

        /**
         * Constructor not using the whole sound as a musical note
         *
         * @param step1
         *            the iteration step value
         * @param useRawData1
         *            use double array of arrays instead of spectrums
         */
        public HarmonicProductSpectrumFrequencySoundTransform (final double step1, final boolean useRawData1) {
            this (false, step1, -1, useRawData1);
        }

        /**
         * Constructor not using the whole sound as a musical note
         *
         * @param step1
         *            the iteration step value
         * @param windowLength1
         *            length of the spectrum used during each iteration (the
         *            highest the slowest)
         * @param useRawData1
         *            use double array of arrays instead of spectrums
         */
        @SuppressWarnings ("unchecked")
        public HarmonicProductSpectrumFrequencySoundTransform (final boolean note1, final double step1, final int windowLength1, final boolean useRawData1) {
            this.spectrumHelper = $.select (SpectrumHelper.class);
            this.step = step1;
            this.note = note1;
            this.windowLength = windowLength1;
            this.useRawData = useRawData1;
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
        public Spectrum<T> transformFrequencies (final Spectrum<T> spectrum, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {
            this.transformFrequencies (spectrum, spectrum.getSampleRate (), offset, powOf2NearestLength, length, soundLevelInDB);
            return null;
        }

        @Override
        public void transformFrequencies (final double [] [] spectrum, final float sampleRate, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {
            this.transformFrequencies ((Object)spectrum, sampleRate, offset, powOf2NearestLength, length, soundLevelInDB);
        }
        
        public void transformFrequencies (final Object spectrum, final float sampleRate, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

            final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.soundLength / this.step));
            if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.soundLength / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.soundLength / this.step), percent));
            }
            float f0 = 0;

            if (soundLevelInDB > 30 || this.note) {

                final float [] peaks = new float [10];
                for (int i = 1 ; i <= 10 ; i++) {
                    peaks [i - 1] = this.f0 (spectrum, sampleRate, i);
                }
                Arrays.sort (peaks);
                f0 = this.bestCandidate (peaks);
            }

            if (this.note) {
                this.detectedNoteVolume = soundLevelInDB;
            }
            this.loudestfreqs [(int) (offset / this.step)] = f0;

        }

        /**
         * Find the f0 (fundamental frequency) using the Harmonic Product
         * Spectrum
         *
         * @param fs
         *            spectrum at a specific time
         * @param sampleRate if the passed spectrum is in raw data (as a double [] [])
         * @param hpsfactor
         *            number of times to multiply the frequencies together
         * @return a fundamental frequency (in Hz)
         */
        @SuppressWarnings ("unchecked")
        public float f0 (final Object spectrum, final float sampleRate, final int hpsfactor) {
            if (spectrum instanceof Spectrum){
                return this.f0WithSpectrum ((Spectrum<T>)spectrum, hpsfactor);
            }
            return this.f0WithRawData ((double [] []) spectrum, sampleRate, hpsfactor);
        }

        private float f0WithRawData (double [] [] spectrumAsDoubles, float sampleRate, int hpsfactor) {
            double [] productOfMultiples = this.spectrumHelper.productOfMultiples (spectrumAsDoubles, sampleRate, hpsfactor, HarmonicProductSpectrumFrequencySoundTransform.PART_OF_THE_SPECTRUM_TO_READ);
            final int spectrumLength = spectrumAsDoubles [0].length;
            final int maxIndex = this.spectrumHelper.getMaxIndex (productOfMultiples, 0, (int) (spectrumLength * HarmonicProductSpectrumFrequencySoundTransform.PART_OF_THE_SPECTRUM_TO_READ) / hpsfactor);
            return this.spectrumHelper.freqFromSampleRate (maxIndex, spectrumLength * HarmonicProductSpectrumSoundTransform.TWICE / hpsfactor, sampleRate);
        }

        private float f0WithSpectrum (Spectrum<T> spectrum, int hpsfactor) {
            final Spectrum<T> productOfMultiples = this.spectrumHelper.productOfMultiples (spectrum, hpsfactor, HarmonicProductSpectrumFrequencySoundTransform.PART_OF_THE_SPECTRUM_TO_READ);
            final int spectrumLength = this.spectrumHelper.getLengthOfSpectrum (spectrum);
            final int maxIndex = this.spectrumHelper.getMaxIndex (productOfMultiples, 0, (int) (spectrumLength * HarmonicProductSpectrumFrequencySoundTransform.PART_OF_THE_SPECTRUM_TO_READ) / hpsfactor);
            return this.spectrumHelper.freqFromSampleRate (maxIndex, spectrumLength * HarmonicProductSpectrumSoundTransform.TWICE / hpsfactor, spectrum.getSampleRate ());
        }

        @Override
        public boolean isReverseNecessary () {
            return false;
        }
        
        @Override
        public boolean rawSpectrumPrefered () {
            return this.useRawData;
        }
    }

    private final HarmonicProductSpectrumFrequencySoundTransform<T> decoratedTransform;

    /**
     * Default constructor
     *
     * @param note1
     *            if true, the whole sound will be transformed at once to know
     *            the loudest freq. therefore the array will be of size 1.
     * @param useRawData1
     *            use double array of arrays instead of spectrums
     */
    public HarmonicProductSpectrumSoundTransform (final boolean note1, final boolean useRawData1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (note1, useRawData1);
    }

    /**
     * Constructor not using the whole sound as a musical note
     *
     * @param step1
     *            the iteration step value
     * @param useRawData1
     *            use double array of arrays instead of spectrums
     */
    public HarmonicProductSpectrumSoundTransform (final double step1, final boolean useRawData1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (step1, useRawData1);
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
     * @param useRawData1
     *            use double array of arrays instead of spectrums
     */
    public HarmonicProductSpectrumSoundTransform (final boolean note1, final double step1, final int windowLength1, final boolean useRawData1) {
        this.decoratedTransform = new HarmonicProductSpectrumFrequencySoundTransform<T> (note1, step1, windowLength1, useRawData1);
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
