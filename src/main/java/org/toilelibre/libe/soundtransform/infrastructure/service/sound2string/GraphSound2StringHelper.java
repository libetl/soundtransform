package org.toilelibre.libe.soundtransform.infrastructure.service.sound2string;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ToStringSoundTransformation;

public class GraphSound2StringHelper implements Sound2StringHelper {

    private static final int ARBITRARY_LENGTH = 8000;
    private static final int ARBITRARY_HEIGHT = 20;
    
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
        return new ToStringSoundTransformation (GraphSound2StringHelper.ARBITRARY_LENGTH, GraphSound2StringHelper.ARBITRARY_HEIGHT).toString (input);
    }
}
