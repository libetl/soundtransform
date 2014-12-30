package org.toilelibre.libe.soundtransform.actions.play;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;


public class PlaySound extends Action {


	public void play (final Sound[] channels) throws PlaySoundException {
		final PlaySoundService ps = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.PlaySoundClipImpl ();
		ps.play (channels);
	}
}
