package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface ADSRHelper {

    int findDecay (Sound channel1, int attack) throws SoundTransformException;

    int findRelease (Sound channel1) throws SoundTransformException;

    int findSustain (Sound channel1, int decay) throws SoundTransformException;

}
