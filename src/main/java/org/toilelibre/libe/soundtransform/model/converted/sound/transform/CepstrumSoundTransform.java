package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.PianoFrequency;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

/**
 * Transforms a sound into a list of cepstrums (log modulus of the spectrums).
 * Useful to get the f0 values of a sound (loudest freqs array).
 *
 * <p>
 * The obtained Spectrum are not really spectrums. They consist of a graph a
 * quefrencies (and not frequencies).
 * </p>
 * <p>
 * The peak can represent the f0 (if the FormatInfo of the input sound is
 * adequate), but it is not faithful everytime.
 * </p>
 * This method can detect wrong values.
 *
 * @param <T>
 *            The kind of object held inside a spectrum.
 */
public class CepstrumSoundTransform<T extends Serializable> extends AbstractLogAware<CepstrumSoundTransform<T>> implements PeakFindSoundTransform<T, AbstractLogAware<CepstrumSoundTransform<T>>> {
    static class CepstrumFrequencySoundTransform<T extends Serializable> extends SimpleFrequencySoundTransform<T> {

        private final double                      step;
        private float []                          loudestfreqs;
        private int                               index;
        private int                               length;
        private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
        private final SpectrumHelper<T>           spectrumHelper;
        private static final float                MIN_VOICE_FREQ                     = PianoFrequency.PianoValues.E2.getFrequency ();
        private static final float                MAX_VOICE_FREQ                     = PianoFrequency.PianoValues.C6.getFrequency ();
        private static final float                A_CONSTANT_TO_REDUCE_OCTAVE_ERRORS = 0.6f;
        private static final float                UNLIKELY_TO_BE_HEARD_DELTA         = 50;
        private static final float                SOUND_LEVEL_THRESHOLD_IN_DB        = 30;
        private final boolean                     note;

        private float                             detectedNoteVolume;

        @SuppressWarnings ("unchecked")
        CepstrumFrequencySoundTransform (final double step1, final boolean note1) {
            super ();
            this.step = step1;
            this.note = note1;
            this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
            this.spectrumHelper = $.select (SpectrumHelper.class);
        }

        public float [] getLoudestFreqs () {
            return this.loudestfreqs.clone ();
        }

        @Override
        public double getStep (final double defaultValue) {
            if (this.note) {
                return this.length;
            }
            return this.step;
        }

        @Override
        public int getWindowLength (final double freqmax) {
            if (this.note) {
                return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)) + 1);
            }
            return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)) + 1);
        }

        @Override
        public Channel initSound (final Channel input) {
            this.index = 0;
            this.length = input.getSamplesLength ();
            if (this.note) {
                this.loudestfreqs = new float [1];
            } else {
                this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
            }
            return super.initSound (input);
        }

        @Override
        public boolean isReverseNecessary () {
            return false;
        }

        @Override
        public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

            final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.length / this.step));
            if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.length / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.length / this.step), percent));
            }

            final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);

            this.loudestfreqs [this.index] = this.checkIfLikelyToBeHeard (soundLevelInDB, this.findLoudestFreqFromCepstrum (fscep));
            this.index++;

            if (this.note) {
                this.detectedNoteVolume = soundLevelInDB;
            }
            return fscep;
        }

        private float checkIfLikelyToBeHeard (final float soundlevelInDb, final float possibleLoudestFreq) {
            return soundlevelInDb < CepstrumFrequencySoundTransform.SOUND_LEVEL_THRESHOLD_IN_DB || Math.abs (CepstrumFrequencySoundTransform.MAX_VOICE_FREQ - possibleLoudestFreq) < CepstrumFrequencySoundTransform.UNLIKELY_TO_BE_HEARD_DELTA ? 0 : possibleLoudestFreq;
        }

        private float findLoudestFreqFromCepstrum (final Spectrum<T> cepstrum) {
            final int high = (int) this.frequencyToCepstrumIndex (CepstrumFrequencySoundTransform.MIN_VOICE_FREQ, cepstrum);
            final int low = (int) this.frequencyToCepstrumIndex (CepstrumFrequencySoundTransform.MAX_VOICE_FREQ, cepstrum);

            final double maxValue = this.spectrumHelper.getMaxValue (cepstrum, low, high);
            final double thresholdValue = maxValue - (1 - CepstrumFrequencySoundTransform.A_CONSTANT_TO_REDUCE_OCTAVE_ERRORS) * maxValue * maxValue;
            final float maxIndex = this.spectrumHelper.getFirstPeak (cepstrum, low, high, thresholdValue);
            return this.cepstrumIndexToFrequency ((int) maxIndex, cepstrum);
        }

        private float frequencyToCepstrumIndex (final float frequency, final Spectrum<T> cepstrum) {
            final float spectrumLength = this.spectrumHelper.getLengthOfSpectrum (cepstrum);
            final float timelapseInTheCepstrum = spectrumLength * 1.0f / cepstrum.getSampleRate ();
            return (float) (1.0 * spectrumLength / (frequency * timelapseInTheCepstrum));
        }

        private float cepstrumIndexToFrequency (final int quefrency, final Spectrum<T> cepstrum) {
            final float spectrumLength = this.spectrumHelper.getLengthOfSpectrum (cepstrum);
            final float timelapseInTheCepstrum = spectrumLength * 1.0f / cepstrum.getSampleRate ();
            return (float) (1.0 / (quefrency / spectrumLength * timelapseInTheCepstrum));
        }

        public float getDetectedNoteVolume () {
            return this.detectedNoteVolume;
        }
    }

    private final CepstrumFrequencySoundTransform<T> decoratedTransform;

    /**
     * Default Constructor
     *
     * @param step
     *            the iteration step value (increasing the value will speed the
     *            transform but will be less precise)
     * @param note
     *            if true, the loudest freqs array will contain a single element
     *            and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransform (final double step, final boolean note) {
        super ();
        this.decoratedTransform = new CepstrumFrequencySoundTransform<T> (step, note);
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
    public CepstrumSoundTransform<T> setObservers (final Observer... observers1) {
        this.decoratedTransform.setObservers (observers1);
        return this;
    }

}
