package org.toilelibre.libe.soundtransform.objects;

import org.toilelibre.libe.soundtransform.objects.Sound;

public interface Note {

	Sound [] getAttack (int frequency, int length);

	Sound [] getDecay (int frequency, int length);

	Sound [] getSustain (int frequency, int length);

	Sound [] getRelease (int frequency, int length);

	int getFrequency ();
}
