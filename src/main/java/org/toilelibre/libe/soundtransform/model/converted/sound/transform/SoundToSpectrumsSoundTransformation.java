package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class SoundToSpectrumsSoundTransformation<T> extends SimpleFrequencySoundTransformation<T> {

    private int                  threshold;
    private int                  channel;
    private final List<Spectrum<?> []> spectrums;
    private int                  index;

    public SoundToSpectrumsSoundTransformation () {
        super ();
        this.spectrums = new ArrayList<Spectrum<?> []> ();
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    public List<Spectrum<?> []> getSpectrums () {
        return this.spectrums;
    }

    @Override
    public Sound initSound (final Sound input) {
        this.index = 0;
        this.channel = input.getChannelNum ();
        int roundedSize = 2;
        while (input.getSampleRate () > roundedSize) {
            roundedSize *= 2;
        }
        this.threshold = roundedSize;
        final int spectrumsSize = (int) Math.ceil (input.getSamples ().length * 1.0 / roundedSize);
        this.spectrums.add (new Spectrum [spectrumsSize]);
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs) {
        this.spectrums.get (this.channel) [this.index++] = fs;
        return fs;
    }
}
