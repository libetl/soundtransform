package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class CepstrumSoundTransformation extends SimpleFrequencySoundTransformation {

    private double                        threshold;
    private int []                        loudestfreqs;
    private int                           index;
    private int                           length;
    private static int                    shortSoundLength = 9000;
    private final Spectrum2CepstrumHelper spectrum2CepstrumHelper;
    private final SpectrumHelper          spectrumHelper;

    public CepstrumSoundTransformation (FourierTransformHelper helper1, Spectrum2CepstrumHelper helper2, SpectrumHelper helper3) {
        super (helper1);
        this.threshold = 100;
        this.spectrum2CepstrumHelper = helper2;
        this.spectrumHelper = helper3;
    }

    public CepstrumSoundTransformation (FourierTransformHelper helper1, Spectrum2CepstrumHelper helper2, SpectrumHelper helper3, final double threshold) {
        super (helper1);
        this.threshold = threshold;
        this.spectrum2CepstrumHelper = helper2;
        this.spectrumHelper = helper3;
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
