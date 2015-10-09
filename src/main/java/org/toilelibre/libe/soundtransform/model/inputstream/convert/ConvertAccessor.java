package org.toilelibre.libe.soundtransform.model.inputstream.convert;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamAccessor;

public abstract class ConvertAccessor extends InputStreamAccessor {

    public ConvertAccessor () {
        super ();
        this.usedImpls.put (FormatConvertService.class, GuessConverterFormatConvertService.class);
    }
}
