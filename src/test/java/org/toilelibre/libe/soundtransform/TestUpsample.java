package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.format.AudioFileHelper;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.sound.SoundAppender;

public class TestUpsample {

	@Test
	public void testTransform8363Hz1BitSoundInto44100Hz2BitsSound () {

		try {
			ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
			File input = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
			File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
			Sound [] inputSounds = new TransformSound ().fromInputStream (AudioFileHelper.getAudioInputStream (input));
			Sound [] outputSounds = new Sound [inputSounds.length];
			for (int i = 0 ; i < inputSounds.length ; i++){
				Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds [i], 2);
				tmp = SoundAppender.resizeToSampleRate (tmp, 44100);
				outputSounds [i] = tmp;
			}
			
			AudioInputStream ais = new TransformSound ().toStream (outputSounds, new AudioFormat (44100, 2, outputSounds.length, true, false));


			AudioSystem.write (ais, AudioFileFormat.Type.WAVE, output);
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testTransform11025Hz2BitsSoundInto44100Hz2BitsSound () {

		try {
			ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
			File input = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
			File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
			Sound [] inputSounds = new TransformSound ().fromInputStream (AudioFileHelper.getAudioInputStream (input));
			Sound [] outputSounds = new Sound [inputSounds.length];
			for (int i = 0 ; i < inputSounds.length ; i++){
				Sound tmp = SoundAppender.changeNbBytesPerSample (inputSounds [i], 2);
				tmp = SoundAppender.resizeToSampleRate (tmp, 44100);
				outputSounds [i] = tmp;
			}
			
			AudioInputStream ais = new TransformSound ().toStream (outputSounds, new AudioFormat (44100, 2 * 8, outputSounds.length, true, false));


			AudioSystem.write (ais, AudioFileFormat.Type.WAVE, output);
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
