package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface ADSRHelper {
    
    int findDecay (final double [] magnitudeArray, int attack) throws SoundTransformException;

    int findRelease (final double [] magnitudeArray) throws SoundTransformException;

    int findSustain (final double [] magnitudeArray, int decay) throws SoundTransformException;
}
