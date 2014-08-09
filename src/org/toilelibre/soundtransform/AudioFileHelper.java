package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;

public class AudioFileHelper {
	
	private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private static File tmpfile = new File (classLoader.getResource("before.wav").getFile());
	
	public static AudioInputStream getAudioInputStream(File inputFile) throws UnsupportedAudioFileException, IOException {
		File f = inputFile;
		if (inputFile.getName().toLowerCase().endsWith(".mp3")) {
			AudioInputStream ais = new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader()
					.getAudioInputStream(inputFile);
			AudioFormat cdFormat = new AudioFormat (44100, 16, 2, true, false);
			DecodedMpegAudioInputStream decodedais = new DecodedMpegAudioInputStream (cdFormat, ais);
			AudioSystem.write(decodedais, AudioFileFormat.Type.WAVE, AudioFileHelper.tmpfile);
			f = AudioFileHelper.tmpfile;
		}
		return AudioSystem.getAudioInputStream(f);
	}
}
