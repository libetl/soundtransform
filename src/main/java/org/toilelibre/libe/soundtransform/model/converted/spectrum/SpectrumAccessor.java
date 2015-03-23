package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAccessor;

public abstract class SpectrumAccessor extends SoundAccessor {

    public SpectrumAccessor () {
        super ();
        this.usedImpls.put (SpectrumToStringService.class, DefaultSpectrumToStringService.class);
    }
}
