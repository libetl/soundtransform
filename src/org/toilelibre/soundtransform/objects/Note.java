package org.toilelibre.soundtransform.objects;

import org.toilelibre.soundtransform.objects.Sound;

public interface Note {

	Sound [] getAttack (int frequency, int length);

	Sound [] getDecay (int frequency, int length);

	Sound [] getSustain (int frequency, int length);

	Sound [] getRelease (int frequency, int length);
}
