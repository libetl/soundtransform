package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class Spectrum<T> {

    private final T          state;
    private final FormatInfo formatInfo;

    public Spectrum (final T state1, final FormatInfo formatInfo1) {
        super ();
        this.state = state1;
        this.formatInfo = formatInfo1;
    }

    public FormatInfo getFormatInfo () {
        return this.formatInfo;
    }

    public float getSampleRate () {
        return this.formatInfo.getSampleRate ();
    }

    public int getSampleSize () {
        return this.formatInfo.getSampleSize ();
    }

    public T getState () {
        return this.state;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public String toString () {
        return $.create (Spectrum2StringService.class).convert (this);
    }
}
