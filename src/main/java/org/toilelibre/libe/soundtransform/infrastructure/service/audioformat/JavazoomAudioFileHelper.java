package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;

public class JavazoomAudioFileHelper implements AudioFileHelper {

	@Override
    public AudioInputStream getAudioInputStream (final File inputFile) throws UnsupportedAudioFileException, IOException {
		File readFile = inputFile;
		if (inputFile.getName ().toLowerCase ().endsWith (".mp3")) {
			final File tempFile =  File.createTempFile("soundtransform", ".wav");
			final AudioInputStream ais = new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader ().getAudioInputStream (inputFile);
			final AudioFormat cdFormat = new AudioFormat (44100, 16, 2, true, false);
			final javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream decodedais = new javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream (cdFormat, ais);
			AudioSystem.write (decodedais, AudioFileFormat.Type.WAVE, tempFile);
			readFile = tempFile;
		}
		return AudioSystem.getAudioInputStream (readFile);
	}
}
