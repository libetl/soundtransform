package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.soundtransform.transforms.EightBitsSoundTransformation;
import org.toilelibre.soundtransform.transforms.EqualizerSoundTransformation;
import org.toilelibre.soundtransform.transforms.ReverseSoundTransformation;

public class WavTest {

	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private File input  = new File (classLoader.getResource("before.wav").getFile());
	private File output = new File (new File (
			classLoader.getResource("before.wav").getFile()).getParent() + "/after.wav");
	
	@Test
	public void test8bits (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformWav (input, output,
					new EightBitsSoundTransformation(25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void testRemoveLowFreqs (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformWav (input, output,
			    new EqualizerSoundTransformation(
				  new double [] {0, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 16000, 18000, 22050},
				  new double [] {0,    0,  0.1,  0.3,  0.7,     1,     1,     1,     1,     1,     1}));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReverse (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformWav (input, output,
			    new ReverseSoundTransformation());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
