package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface Note {

    Sound getAttack (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Sound getDecay (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    float getFrequency ();

    String getName ();

    Sound getRelease (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Sound getSustain (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;
    
}
