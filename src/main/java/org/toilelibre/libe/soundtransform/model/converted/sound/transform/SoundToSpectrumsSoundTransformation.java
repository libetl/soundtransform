package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class SoundToSpectrumsSoundTransformation<T> extends SimpleFrequencySoundTransformation<T> {

    private int               threshold;
    private List<Spectrum<T>> spectrums;

    public SoundToSpectrumsSoundTransformation () {
        super ();
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    public Spectrum<?> [] getSpectrums () {
        return this.spectrums.toArray (new Spectrum [this.spectrums.size ()]);
    }

    @Override
    public Sound initSound (final Sound input) {
        int roundedSize = 2;
        while (input.getSampleRate () > roundedSize) {
            roundedSize *= 2;
        }
        this.threshold = roundedSize;
        this.spectrums = new ArrayList<Spectrum<T>> ();
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs) {
        this.spectrums.add (fs);
        return fs;
    }
}
