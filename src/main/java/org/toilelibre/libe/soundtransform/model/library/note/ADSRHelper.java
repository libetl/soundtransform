package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface ADSRHelper {

    int findDecay (Channel channel1, int attack) throws SoundTransformException;

    int findRelease (Channel channel1) throws SoundTransformException;

    int findSustain (Channel channel1, int decay) throws SoundTransformException;

}
