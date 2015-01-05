package org.toilelibre.libe.soundtransform.infrastructure.service.sound2string;

import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ToStringSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringHelper;

public class GraphSound2StringHelper implements Sound2StringHelper {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.infrastructure.service.sound2string
     * .Sound2StringHelper
     * #process(org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public String process (final Sound input) {
        return new ToStringSoundTransformation (8000, 20).toString (input);
    }
}
