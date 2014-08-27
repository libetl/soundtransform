package org.toilelibre.soundtransform.objects;

import org.toilelibre.soundtransform.objects.Sound;

public interface Note {

	Sound[] getAttack (int frequency);
	Sound[] getDecay (int frequency);
	Sound[] getSustain (int frequency);
	Sound[] getRelease (int frequency);
}
