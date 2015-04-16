package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation.PeakFindWithHPSSoundTransformationEventCode;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

public class CepstrumSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> implements PeakFindSoundTransformation<T> {

    private final double                      step;
    private float []                          loudestfreqs;
    private int                               index;
    private int                               length;
    private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
    private final SpectrumHelper<T>           spectrumHelper;
    private final List<Spectrum<T>>           cepstrums;
    private static final int                  MIN_VOICE_FREQ     = 40;
    private static final int                  MAX_VOICE_FREQ     = 1000;
    private final boolean keepCepstrums;
    private final boolean note;

    private float                             detectedNoteVolume;

    public CepstrumSoundTransformation () {
        this (100, false, false);
    }

    public CepstrumSoundTransformation (final boolean note1) {
        this (100, false, note1);
    }

    public CepstrumSoundTransformation (final double step1) {
        this (step1, false, false);
    }

    public CepstrumSoundTransformation (final double step1, final boolean note1) {
        this (step1, false, note1);
    }

    @SuppressWarnings ("unchecked")
    public CepstrumSoundTransformation (final double step1, boolean keepCepstrums1, final boolean note1) {
        super ();
        this.step = step1;
        this.note = note1;
        this.keepCepstrums = keepCepstrums1;
        this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
        this.spectrumHelper = $.select (SpectrumHelper.class);
        this.cepstrums = new LinkedList<Spectrum<T>> ();
    }

    @Override
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
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

        final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.length / this.step));
        if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.length / this.step))) {
            this.log (new LogEvent (PeakFindWithHPSSoundTransformationEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.length / this.step), percent));
        }

        final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);
        if (this.keepCepstrums){
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
