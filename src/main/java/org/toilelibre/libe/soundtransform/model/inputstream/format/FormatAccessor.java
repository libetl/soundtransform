package org.toilelibre.libe.soundtransform.model.inputstream.format;

import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConvertAccessor;

public abstract class FormatAccessor extends ConvertAccessor {

    public FormatAccessor () {
        super ();
        this.usedImpls.put (AudioFormatService.class, DefaultAudioFormatService.class);
    }
}
