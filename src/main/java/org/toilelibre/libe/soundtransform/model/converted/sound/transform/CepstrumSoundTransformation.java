package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;

public class CepstrumSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> {

    private final double                      step;
    private int []                            loudestfreqs;
    private int                               index;
    private int                               length;
    private static final int                  SHORT_SOUND_LENGTH = 9000;
    private final SpectrumToCepstrumHelper<T> spectrum2CepstrumHelper;
    private final SpectrumHelper<T>           spectrumHelper;

    public CepstrumSoundTransformation () {
        this (100);
    }

    @SuppressWarnings ("unchecked")
    public CepstrumSoundTransformation (final double step1) {
        super ();
        this.step = step1;
        this.spectrum2CepstrumHelper = $.select (SpectrumToCepstrumHelper.class);
        this.spectrumHelper = $.select (SpectrumHelper.class);
    }

    public int [] getLoudestFreqs () {
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
        this.loudestfreqs = new int [(int) (input.getSamplesLength () / this.step) + 1];
        this.index = 0;
        this.length = input.getSamplesLength ();
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            this.loudestfreqs = new int [1];
        } else {
            this.loudestfreqs = new int [(int) (input.getSamplesLength () / this.step) + 1];
        }
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs) {

        final Spectrum<T> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);

        this.loudestfreqs [this.index] = this.spectrumHelper.getMaxIndex (fscep, 0, (int) fs.getSampleRate ());
        this.index++;

        return fscep;
    }
}
