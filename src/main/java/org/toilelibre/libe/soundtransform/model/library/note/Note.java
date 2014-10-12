package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface Note {

	String getName ();

	Sound getAttack (int frequency, int channelnum, int length);

	Sound getDecay (int frequency, int channelnum, int length);

	Sound getSustain (int frequency, int channelnum, int length);

	Sound getRelease (int frequency, int channelnum, int length);

	int getFrequency ();
}
