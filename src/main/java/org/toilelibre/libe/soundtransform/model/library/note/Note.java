package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface Note {

    Channel getAttack (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Channel getDecay (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    float getFrequency ();

    String getName ();

    Channel getRelease (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

    Channel getSustain (float frequency, int channelnum, float lengthInSeconds) throws SoundTransformException;

}
