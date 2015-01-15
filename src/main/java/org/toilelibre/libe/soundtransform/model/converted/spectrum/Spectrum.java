package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;

public class Spectrum {

    private final Complex [] state;
    private final int        sampleRate;
    private int              nbBytes;

    public Spectrum (final Complex [] state, final int sampleRate, final int nbBytes) {
        super ();
        this.state = state;
        this.sampleRate = sampleRate;
        this.nbBytes = nbBytes;
    }

    public int getNbBytes () {
        return this.nbBytes;
    }

    public int getSampleRate () {
        return this.sampleRate;
    }

    public Complex [] getState () {
        return this.state;
    }

    public void setNbBytes (final int nbBytes) {
        this.nbBytes = nbBytes;
    }

    @Override
    public String toString () {
        return new Spectrum2StringService ($.select (SpectrumToStringHelper.class)).convert (this);
    }
}
