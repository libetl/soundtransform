package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

/**
 * Transforms a sound into a list of cepstrums (log modulus of the spectrums).
 * Useful to get the f0 values of a sound (loudest freqs array).
 *
 * The obtained Spectrum are not really spectrums. They consist of a graph a
 * quefrencies (and not frequencies).<br/>
 * The peak can represent the f0 (if the FormatInfo of the input sound is
 * adequate), but it is not faithful everytime.<br/>
 * This method can detect wrong values.
 *
 * @param <T>
 *            The kind of object held inside a spectrum.
 */
public class CepstrumSoundTransform<T extends Serializable> extends AbstractLogAware<CepstrumSoundTransform<T>> implements PeakFindSoundTransform<T, AbstractLogAware<CepstrumSoundTransform<T>>> {
    static class CepstrumFrequencySoundTransform<T extends Serializable> extends SimpleFrequencySoundTransform<T> {

        private final double                      step;
        private List<float []>                    allLoudestFreqs;
        private float []                          loudestfreqs;
        private int                               index;
        private int                               length;
        private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
        private final SpectrumHelper<T>           spectrumHelper;
        private static final int                  MIN_VOICE_FREQ = 40;
        private static final int                  MAX_VOICE_FREQ = 1000;
        private final boolean                     note;

        private float                             detectedNoteVolume;

        @SuppressWarnings ("unchecked")
        CepstrumFrequencySoundTransform (final double step1, final boolean note1) {
            super ();
            this.step = step1;
            this.note = note1;
            this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
            this.spectrumHelper = $.select (SpectrumHelper.class);
            this.allLoudestFreqs = new LinkedList<float []> ();
        }

        public float [] getLoudestFreqs () {
            return this.loudestfreqs.clone ();
        }

        public List<float []> getAllLoudestFreqs () {
            return this.allLoudestFreqs;
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
                return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)));
            }
            return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
        }

        @Override
        public Channel initSound (final Channel input) {
            this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
            this.index = 0;
            this.length = input.getSamplesLength ();
            if (this.note) {
                this.loudestfreqs = new float [1];
            } else {
                this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
            }
            this.allLoudestFreqs.add (this.loudestfreqs);
            return super.initSound (input);
        }

        @Override
        public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

            final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.length / this.step));
            if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.length / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.length / this.step), percent));
            }

            final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);

            this.loudestfreqs [this.index] = this.findLoudestFreqFromCepstrum (fscep);
            this.index++;

            if (this.note) {
                this.detectedNoteVolume = soundLevelInDB;
            }
            return fscep;
        }

        private float findLoudestFreqFromCepstrum (final Spectrum<T> fscep) {
            final float spectrumLength = this.spectrumHelper.getLengthOfSpectrum (fscep);
            final float timelapseInTheCepstrum = spectrumLength * 1.0f / fscep.getSampleRate ();
            final float maxIndex = this.spectrumHelper.getMaxIndex (fscep, CepstrumFrequencySoundTransform.MIN_VOICE_FREQ, CepstrumFrequencySoundTransform.MAX_VOICE_FREQ);
            final float t0 = maxIndex / spectrumLength * timelapseInTheCepstrum;
            return 1.0f / t0;
        }

        public float getDetectedNoteVolume () {
            return this.detectedNoteVolume;
        }
    }

    private CepstrumFrequencySoundTransform<T> decoratedTransform;

    /**
     * Constructor with default values. The cepstrums will not be kept when
     * using the getCepstrums method
     */
    public CepstrumSoundTransform () {
        this (100, false);
    }

    /**
     * Constructor with default values. The cepstrums will not be kept when
     * using the getCepstrums method
     * 
     * @param note1
     *            if true, the loudest freqs array will contain a single element
     *            and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransform (final boolean note1) {
        this (100, note1);
    }

    /**
     * The cepstrums will not be kept when using the getCepstrums method
     * 
     * @param step1
     *            the iteration step value (increasing the value will speed the
     *            transform but will be less precise)
     */
    public CepstrumSoundTransform (final double step1) {
        this (step1, false);
    }

    /**
     * Constructor with every parameter specified
     * 
     * @param step1
     *            the iteration step value (increasing the value will speed the
     *            transform but will be less precise)
     * @param note1
     *            if true, the loudest freqs array will contain a single element
     *            and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransform (final double step1, final boolean note1) {
        super ();
        this.decoratedTransform = new CepstrumFrequencySoundTransform<T> (step1, note1);
    }

    @Override
    public float [] transform (Channel input) throws SoundTransformException {
        this.decoratedTransform.transform (input);
        return this.decoratedTransform.getLoudestFreqs ();
    }

    @Override
    public float getDetectedNoteVolume () {
        return this.decoratedTransform.getDetectedNoteVolume ();
    }

}
