package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.soundtransform.transforms.EightBitsSoundTransformation;

public class WavTest {

	@Test
	public void test8bits (){
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			new TransformSound(new PrintlnTransformObserver()).transformWav(
					new File (classLoader.getResource("before.wav").getFile()), 
					new File (classLoader.getResource("before.wav").getPath() + "after.wav"), 
					new EightBitsSoundTransformation(25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
