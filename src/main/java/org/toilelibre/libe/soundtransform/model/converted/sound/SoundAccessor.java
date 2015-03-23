package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.ioc.RootModuleWithoutAccessor;

public abstract class SoundAccessor extends RootModuleWithoutAccessor {

    public SoundAccessor () {
        this.usedImpls.put (SoundPitchAndTempoService.class, DefaultSoundPitchAndTempoService.class);
        this.usedImpls.put (ModifySoundService.class, DefaultModifySoundService.class);
        this.usedImpls.put (SoundToStringService.class, DefaultSoundToStringService.class);
        this.usedImpls.put (CallTransformService.class, DefaultCallTransformService.class);
    }
}
