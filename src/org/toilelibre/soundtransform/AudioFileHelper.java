package org.toilelibre.soundtransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.soundtransform.transforms.SoundTransformation;

public class AudioFileHelper {

	private static String tmpfile = "before.wav";
	
	private static void copyFile(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	public static void transform(String inputFile, String outputFile,
			SoundTransformation... sts) throws IOException,
			UnsupportedAudioFileException {
		//TODO : detect file extension and convert
		File fParam = new File(inputFile);
		File fOrigin = new File(AudioFileHelper.tmpfile);
		File fDest = new File(outputFile);
		AudioFileHelper.copyFile(fParam, fOrigin);
		new TransformSound(new PrintlnTransformObserver()).transformWav(
				fOrigin, fDest, sts);
	}
}
