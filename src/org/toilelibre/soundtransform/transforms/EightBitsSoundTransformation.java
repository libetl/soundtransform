package org.toilelibre.soundtransform.transforms;

import org.toilelibre.soundtransform.objects.Sound;


public class EightBitsSoundTransformation implements SoundTransformation {

	private int step = 1;
	
	public EightBitsSoundTransformation (int step) {
		this.step = step;
    }


	@Override
    public Sound transform (Sound input) {
		
		Sound outputSound = new Sound (new double [input.getSamples ().length],
				input.getNbBytesPerFrame (), input.getFreq());
		for (int i = 0 ; i < input.getSamples ().length ; i+= step){
		  outputSound.getSamples () [i] = input.getSamples () [i];
		}
		
		return outputSound;
    }


}
