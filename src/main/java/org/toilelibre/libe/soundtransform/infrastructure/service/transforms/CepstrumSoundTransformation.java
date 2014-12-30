package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class CepstrumSoundTransformation extends SimpleFrequencySoundTransformation {

    private double       threshold;
    private int []       loudestfreqs;
    private int           index;
    private int           length;
    private static int    shortSoundLength    = 9000;
    private final Spectrum2CepstrumHelper spectrum2CepstrumHelper;
    private final SpectrumHelper spectrumHelper;

    public CepstrumSoundTransformation () {
        this.threshold = 100;
        this.spectrum2CepstrumHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.NaiveSpectrum2CepstrumHelper ();
        this.spectrumHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.HPSSpectrumHelper ();
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
        if (this.length < CepstrumSoundTransformation.shortSoundLength) {
            return this.length;
        }
        return this.threshold;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.length < CepstrumSoundTransformation.shortSoundLength) {
            return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)));
        }
        return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
    }

    @Override
    public Sound initSound (final Sound input) {
        this.loudestfreqs = new int [(int) (input.getSamples ().length / this.threshold) + 1];
        this.index = 0;
        this.length = input.getSamples ().length;
        if (this.length < CepstrumSoundTransformation.shortSoundLength) {
            this.loudestfreqs = new int [1];
        } else {
            this.loudestfreqs = new int [(int) (input.getSamples ().length / this.threshold) + 1];
        }
        return super.initSound (input);
    }

    @Override
    public Spectrum transformFrequencies (final Spectrum fs) {

        final Spectrum fscep = this.spectrum2CepstrumHelper.spectrumToCepstrum (fs);

        this.loudestfreqs [this.index] = this.spectrumHelper.getMaxIndex (fscep, 0, fs.getSampleRate ());
        this.index++;

        return fscep;
    }
}
