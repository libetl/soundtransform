package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;

public class Spectrum<T> {

    private final T   state;
    private final int sampleRate;
    private int       nbBytes;

    public Spectrum (final T state, final int sampleRate, final int nbBytes) {
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

    public T getState () {
        return this.state;
    }

    public void setNbBytes (final int nbBytes) {
        this.nbBytes = nbBytes;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public String toString () {
        return $.create (Spectrum2StringService.class).convert (this);
    }
}
