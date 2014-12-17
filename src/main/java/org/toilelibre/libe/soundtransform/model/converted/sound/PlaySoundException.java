package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

public class PlaySoundException extends Exception {

	public PlaySoundException (LineUnavailableException lineUnavailableException) {
		super (lineUnavailableException);
    }

	public PlaySoundException (IOException e) {
		super (e);
    }

	/**
	 * 
	 */
    private static final long serialVersionUID = -4904836048288493711L;

}
