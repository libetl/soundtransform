package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.soundtransform.transforms.EightBitsSoundTransformation;
import org.toilelibre.soundtransform.transforms.EqualizerSoundTransformation;
import org.toilelibre.soundtransform.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.soundtransform.transforms.PitchSoundTransformation;
import org.toilelibre.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.soundtransform.transforms.SlowdownSoundTransformation;

public class WavTest {

	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private File input  = new File (classLoader.getResource("before.wav").getFile());
	//private File input  = new File ("D:/Mes Soirées 80's-Spécial Discothèques/CD 1/08 Captain Sensible-Wot.mp3");
	private File output = new File (new File (
			classLoader.getResource("before.wav").getFile()).getParent() + "/after.wav");
	
	@Test
	public void test8bits (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
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
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
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
	public void testLinearReg (){
		//will remove the high freqs and smooth the signal
		try {
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
			    new LinearRegressionSoundTransformation(25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReverse (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
			    new ReverseSoundTransformation());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPitch (){
		try {
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
			    new PitchSoundTransformation(85));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSlowdown (){
		//WARN : quite long
		try {
			new TransformSound(new PrintlnTransformObserver()).transformFile (input, output,
			    new SlowdownSoundTransformation(200));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
