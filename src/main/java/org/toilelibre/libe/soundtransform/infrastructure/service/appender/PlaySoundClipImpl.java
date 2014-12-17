package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.toilelibre.libe.soundtransform.actions.transform.ExportSoundToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class PlaySoundClipImpl implements PlaySoundService {

	@Override
	public void play (Sound [] channels) throws PlaySoundException {
		AudioInputStream ais = new ExportSoundToInputStream ().toStream (channels, new AudioFormat (48000, 4, 2, true, false));

		this.play (ais);
	}

	@Override
	public void play (AudioInputStream ais) throws PlaySoundException {
		try {
			Line.Info linfo = new Line.Info (Clip.class);
			Line line = AudioSystem.getLine (linfo);
			final Clip clip = (Clip) line;
			clip.addLineListener (new LineListener () {

				@Override
				public void update (LineEvent event) {
					LineEvent.Type type = event.getType ();
					if (type == LineEvent.Type.OPEN) {
					} else if (type == LineEvent.Type.CLOSE) {
					} else if (type == LineEvent.Type.START) {
					} else if (type == LineEvent.Type.STOP) {
						synchronized (clip){
							clip.close ();
							clip.notify ();
						}
					}

				}

			});
			clip.open (ais);
			clip.start ();
			synchronized (clip){
				clip.wait ();	
			}
		} catch (LineUnavailableException lineUnavailableException) {
			throw new PlaySoundException (lineUnavailableException);
		} catch (IOException e) {
			throw new PlaySoundException (e);
		} catch (InterruptedException e) {
	        e.printStackTrace();
        }
	}

}
