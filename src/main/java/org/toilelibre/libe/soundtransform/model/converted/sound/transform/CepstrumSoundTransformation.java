package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;

public class CepstrumSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> implements PeakFindSoundTransformation<T> {

    private final double                      step;
    private float []                          loudestfreqs;
    private int                               index;
    private int                               length;
    private static final int                  SHORT_SOUND_LENGTH = 9000;
    private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
    private final SpectrumHelper<T>           spectrumHelper;
    private final List<Spectrum<T>>           cepstrums;
    
    private float detectedNoteVolume;

    public CepstrumSoundTransformation () {
        this (100);
    }

    @SuppressWarnings ("unchecked")
    public CepstrumSoundTransformation (final double step1) {
        super ();
        this.step = step1;
        this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
        this.spectrumHelper = $.select (SpectrumHelper.class);
        this.cepstrums = new LinkedList<Spectrum<T>> ();
    }

    public float [] getLoudestFreqs () {
        return this.loudestfreqs.clone ();
    }

    @Override
    public double getStep (final double defaultValue) {
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            return this.length;
        }
        return this.step;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)));
        }
        return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
    }

    @Override
    public Sound initSound (final Sound input) {
        this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
        this.index = 0;
        this.length = input.getSamplesLength ();
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            this.loudestfreqs = new float [1];
        } else {
            this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
        }
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

        final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);
        this.cepstrums.add (fscep);

        this.loudestfreqs [this.index] = this.findLoudestFreqFromCepstrum (fscep);
        this.index++;

        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            this.detectedNoteVolume = soundLevelInDB;
        }
        return fscep;
    }

    private float findLoudestFreqFromCepstrum (Spectrum<T> fscep) {
        final float spectrumLength = spectrumHelper.getLengthOfSpectrum(fscep);
        final float timelapseInTheCepstrum = spectrumLength * 1.0f / fscep.getSampleRate ();
        final float maxIndex = spectrumHelper.getMaxIndex (fscep, 0, (int)fscep.getSampleRate ());
        final float t0 = maxIndex / spectrumLength * timelapseInTheCepstrum;
        return 1.0f / t0;
    }

    @Override
    public float getDetectedNoteVolume () {
        return this.detectedNoteVolume;
    }

    public List<Spectrum<T>> getCepstrums () {
        return cepstrums;
    }
    
}
