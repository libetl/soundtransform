package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class Spectrum<T> implements Serializable {

    private static final long serialVersionUID = -4609743181877386829L;
    
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
        return $.select (SpectrumToStringService.class).convert (this);
    }
}
