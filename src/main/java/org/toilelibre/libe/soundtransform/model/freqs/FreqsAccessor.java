package org.toilelibre.libe.soundtransform.model.freqs;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumAccessor;

public abstract class FreqsAccessor extends SpectrumAccessor {

    public FreqsAccessor (){
        super ();
        this.usedImpls.put(LoudestFreqsService.class, DefaultLoudestFreqsService.class);
    }
}
