package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

public class PlaySoundException extends Exception {

	/**
	 *
	 */
    private static final long serialVersionUID = -4904836048288493711L;

	public PlaySoundException (final InterruptedException e) {
		super (e);
    }

	public PlaySoundException (final IOException e) {
		super (e);
    }

	public PlaySoundException (final LineUnavailableException lineUnavailableException) {
		super (lineUnavailableException);
    }

}
