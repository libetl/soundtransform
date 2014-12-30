package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface Note {

    Sound getAttack (int frequency, int channelnum, float lengthInSeconds);

    Sound getDecay (int frequency, int channelnum, float lengthInSeconds);

    int getFrequency ();

    String getName ();

    Sound getRelease (int frequency, int channelnum, float lengthInSeconds);

    Sound getSustain (int frequency, int channelnum, float lengthInSeconds);
}
