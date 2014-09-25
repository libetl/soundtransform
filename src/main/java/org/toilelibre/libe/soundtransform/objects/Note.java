package org.toilelibre.libe.soundtransform.objects;

import org.toilelibre.libe.soundtransform.objects.Sound;

public interface Note {

    String getName ();
    
	Sound getAttack (int frequency, int channelnum, int length);

	Sound getDecay (int frequency, int channelnum, int length);

	Sound getSustain (int frequency, int channelnum, int length);

	Sound getRelease (int frequency, int channelnum, int length);

	int getFrequency ();
}
