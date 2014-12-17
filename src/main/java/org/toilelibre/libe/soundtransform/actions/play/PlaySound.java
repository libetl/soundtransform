package org.toilelibre.libe.soundtransform.actions.play;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.PlaySoundClipImpl;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;


public class PlaySound extends Action {


	public void play (Sound[] channels) throws PlaySoundException {
		PlaySoundService ps = new PlaySoundClipImpl ();
		ps.play (channels);
	}
}
