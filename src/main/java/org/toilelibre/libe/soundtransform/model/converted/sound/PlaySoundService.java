package org.toilelibre.libe.soundtransform.model.converted.sound;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;


public interface PlaySoundService {

	Object play (Sound[] channels) throws PlaySoundException;
	Object play (AudioInputStream ais) throws PlaySoundException;
	Object play (Spectrum spectrum) throws PlaySoundException;
}
