package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

/**
 * Transforms a sound into a list of cepstrums (log modulus of the spectrums). Useful to get the f0 values of a sound
 * (loudest freqs array).
 * 
 * The obtained Spectrum are not really spectrums. They consist of a graph a quefrencies (and not frequencies).<br/>
 * The peak can represent the f0 (if the FormatInfo of the input sound is adequate), but it is not faithful everytime.<br/>
 * This method can detected false values.
 * 
 * @param <T> The kind of object held inside a spectrum.
 */
public class CepstrumSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> implements PeakFindSoundTransformation<T> {

    private final double                      step;
    private List<float []>                    allLoudestFreqs;
    private float []                          loudestfreqs;
    private int                               index;
    private int                               length;
    private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
    private final SpectrumHelper<T>           spectrumHelper;
    private final List<Spectrum<T>>           cepstrums;
    private static final int                  MIN_VOICE_FREQ = 40;
    private static final int                  MAX_VOICE_FREQ = 1000;
    private final boolean                     keepCepstrums;
    private final boolean                     note;

    private float                             detectedNoteVolume;

    /**
     * Constructor with default values.
     * The cepstrums will not be kept when using the getCepstrums method
     */
    public CepstrumSoundTransformation () {
        this (100, false, false);
    }

    /**
     * Constructor with default values.
     * The cepstrums will not be kept when using the getCepstrums method
     * @param note1 if true, the loudest freqs array will contain a single element
     *              and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransformation (final boolean note1) {
        this (100, false, note1);
    }

    /**
     * The cepstrums will not be kept when using the getCepstrums method
     * @param step1 the iteration step
     *        (increasing the value will speed the transform but will be less precise)
     */
    public CepstrumSoundTransformation (final double step1) {
        this (step1, false, false);
    }

    /**
     * 
     * The cepstrums will not be kept when using the getCepstrums method
     * @param step1 the iteration step
     *        (increasing the value will speed the transform but will be less precise)
     * @param note1 if true, the loudest freqs array will contain a single element
     *              and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransformation (final double step1, final boolean note1) {
        this (step1, false, note1);
    }

    @SuppressWarnings ("unchecked")
    /**
     * Constructor will every parameter specified
     * @param keepCepstrums1 if true, the cepstrums will all be saved after each call to the method transform
     *        This can cause a big memory leak if not used with care. Be vigilant.
     * @param step1 the iteration step
     *        (increasing the value will speed the transform but will be less precise)
     * @param note1 if true, the loudest freqs array will contain a single element
     *              and the cepstrum will be made once, using the whole sound
     */
    public CepstrumSoundTransformation (final double step1, final boolean keepCepstrums1, final boolean note1) {
        super ();
        this.step = step1;
        this.note = note1;
        this.keepCepstrums = keepCepstrums1;
        this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
        this.spectrumHelper = $.select (SpectrumHelper.class);
        this.cepstrums = new LinkedList<Spectrum<T>> ();
        this.allLoudestFreqs = new LinkedList<float []> ();
    }

    @Override
    public float [] getLoudestFreqs () {
        return this.loudestfreqs.clone ();
    }

    @Override
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
    public Sound initSound (final Sound input) {
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
            this.log (new LogEvent (PeakFindSoundTransformationEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.length / this.step), percent));
        }

        final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);
        if (this.keepCepstrums) {
            this.cepstrums.add (fscep);
        }

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
        final float maxIndex = this.spectrumHelper.getMaxIndex (fscep, CepstrumSoundTransformation.MIN_VOICE_FREQ, CepstrumSoundTransformation.MAX_VOICE_FREQ);
        final float t0 = maxIndex / spectrumLength * timelapseInTheCepstrum;
        return 1.0f / t0;
    }

    @Override
    public float getDetectedNoteVolume () {
        return this.detectedNoteVolume;
    }

    public List<Spectrum<T>> getCepstrums () {
        return this.cepstrums;
    }

}
