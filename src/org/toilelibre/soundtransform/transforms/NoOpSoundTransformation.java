package org.toilelibre.soundtransform.transforms;

import org.toilelibre.soundtransform.objects.Sound;

public class NoOpSoundTransformation implements SoundTransformation {
	
	public NoOpSoundTransformation () {
    }


	@Override
	public Sound transform (Sound input) {
		return NoOpSoundTransformation.noop (input);
	}

	private static Sound noop (Sound sound) {
		double [] data = sound.getSamples ();

		// normalized result in newdata
		double [] newdata = new double [data.length];
		
		for (int i = 0 ; i < data.length ; i++){
			newdata [i] = data [i];
		}
		
		return new Sound (newdata, sound.getNbBytesPerFrame (), sound.getFreq());
	}
}
