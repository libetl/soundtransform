package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class SoundToSpectrumsSoundTransformation<T> extends SimpleFrequencySoundTransformation<T> {

    private static final int           TWO = 2;
    private int                        step;
    private int                        channel;
    private final List<Spectrum<?> []> spectrums;
    private int                        index;

    public SoundToSpectrumsSoundTransformation () {
        super ();
        this.spectrums = new ArrayList<Spectrum<?> []> ();
    }

    public List<Spectrum<?> []> getSpectrums () {
        return this.spectrums;
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public Sound initSound (final Sound input) {
        this.index = 0;
        this.channel = input.getChannelNum ();
        int roundedSize = SoundToSpectrumsSoundTransformation.TWO;
        while (input.getSampleRate () > roundedSize) {
            roundedSize *= SoundToSpectrumsSoundTransformation.TWO;
        }
        this.step = roundedSize;
        final int spectrumsSize = (int) Math.ceil (input.getSamplesLength () * 1.0 / roundedSize);
        this.spectrums.add (new Spectrum [spectrumsSize]);
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs) {
        this.spectrums.get (this.channel) [this.index++] = fs;
        return fs;
    }
}
