package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface ADSRHelper {

	int findDecay (Sound channel1, int attack);

	int findRelease (Sound channel1);

	int findSustain (Sound channel1, int decay);

}
