package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.PlaySoundClipImpl;
import org.toilelibre.libe.soundtransform.model.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class TestPlaySound {
	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private File	    input	    = new File (classLoader.getResource ("before.wav").getFile ());

	@Test
	public void playBeforeWav () throws UnsupportedAudioFileException, IOException, PlaySoundException{
		PlaySoundService ps = new PlaySoundClipImpl ();
		ConvertAudioFileService convertAudioFileService = new ConvertAudioFileService ();
		AudioInputStream ais = convertAudioFileService.callConverter (input);
		ps.play (ais);
	}
}
