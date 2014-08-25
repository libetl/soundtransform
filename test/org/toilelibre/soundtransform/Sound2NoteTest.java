package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Sound;
import org.toilelibre.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.soundtransform.transforms.SoundTransformation;


public class Sound2NoteTest {
	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private File	    input	    = new File (classLoader.getResource ("notes/HM_Piano5_C3.wav").getFile ());

	protected int computeMagnitude (FrequenciesState fs) {
		double sum = 0;
		for (int i = 0 ; i < fs.getState ().length ; i++){
			sum += fs.getState () [i].abs ();
		}
        return (int) (sum / fs.getState ().length);
    }
	
	@Test
	public void run () throws UnsupportedAudioFileException, IOException{
		final int threshold = 100;
		AudioInputStream ais = AudioFileHelper.getAudioInputStream (input);
		TransformSound ts = new TransformSound ();
		
		Sound channel1 = ts.fromInputStream (ais) [0];
		Sound reversed = new ReverseSoundTransformation ().transform (channel1);

		final double[] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int endOfRelease = 0;
		
		SoundTransformation magnitudeTransform = new NoOpFrequencySoundTransformation (){
			int arraylength = 0;

			@Override
            protected double getLowThreshold (double defaultValue) {
				return threshold;
            }

			@Override
            public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxFrequency) {
				magnitude [arraylength++] = Sound2NoteTest.this.computeMagnitude (fs);
	            return super.transformFrequencies (fs, offset, powOf2NearestLength, length, maxFrequency);
            }			
			
		};
		
		magnitudeTransform.transform (reversed);
		try {
	 	  MathArrays.checkOrder(magnitude, MathArrays.OrderDirection.INCREASING, true);
		}catch (NonMonotonicSequenceException nmse){
			endOfRelease = nmse.getIndex () * threshold;
			System.out.println (endOfRelease + " " + magnitude [nmse.getIndex () - 1] + " " + magnitude [nmse.getIndex ()]);
			System.out.println (Arrays.toString (magnitude).substring (0, 2000));
		}
		
	}
}
