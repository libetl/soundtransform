package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface Note {

    Sound getAttack (int frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Sound getDecay (int frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    float getFrequency ();

    String getName ();

    Sound getRelease (int frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Sound getSustain (int frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;
}
