package org.toilelibre.libe.soundtransform.infrastructure.service.sound2string;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundToStringHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ToStringSoundTransform;

final class GraphSoundToStringHelper implements SoundToStringHelper {

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
    public String process (final Channel input) {
        return new ToStringSoundTransform (GraphSoundToStringHelper.ARBITRARY_LENGTH, GraphSoundToStringHelper.ARBITRARY_HEIGHT).toString (input);
    }
}
