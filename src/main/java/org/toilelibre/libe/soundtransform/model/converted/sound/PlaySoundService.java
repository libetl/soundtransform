package org.toilelibre.libe.soundtransform.model.converted.sound;

import javax.sound.sampled.AudioInputStream;


public interface PlaySoundService {

	void play (Sound[] channels) throws PlaySoundException;
	void play (AudioInputStream ais) throws PlaySoundException;
}
