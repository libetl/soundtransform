package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class CepstrumSoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {

    private double                                    threshold;
    private int []                                    loudestfreqs;
    private int                                       index;
    private int                                       length;
    private static final int                          SHORT_SOUND_LENGTH = 9000;
    private final Spectrum2CepstrumHelper<Complex []> spectrum2CepstrumHelper;
    private final SpectrumHelper<Complex []>          spectrumHelper;

    @SuppressWarnings ("unchecked")
    public CepstrumSoundTransformation () {
        super ();
        this.threshold = 100;
        this.spectrum2CepstrumHelper = $.select (Spectrum2CepstrumHelper.class);
        this.spectrumHelper = $.select (SpectrumHelper.class);
    }

    public CepstrumSoundTransformation (final double threshold) {
        this ();
        this.threshold = threshold;
    }

    public int [] getLoudestFreqs () {
        return this.loudestfreqs;
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            return this.length;
        }
        return this.threshold;
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
        this.loudestfreqs = new int [(int) (input.getSamples ().length / this.threshold) + 1];
        this.index = 0;
        this.length = input.getSamples ().length;
        if (this.length < CepstrumSoundTransformation.SHORT_SOUND_LENGTH) {
            this.loudestfreqs = new int [1];
        } else {
            this.loudestfreqs = new int [(int) (input.getSamples ().length / this.threshold) + 1];
        }
        return super.initSound (input);
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs) {

        final Spectrum<Complex []> fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);

        this.loudestfreqs [this.index] = this.spectrumHelper.getMaxIndex (fscep, 0, fs.getSampleRate ());
        this.index++;

        return fscep;
    }
}
