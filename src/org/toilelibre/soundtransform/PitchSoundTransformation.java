package org.toilelibre.soundtransform;

public class PitchSoundTransformation implements SoundTransformation {

	private int percent = 20;
	
	public PitchSoundTransformation (int percent) {
		this.percent = percent;
    }


	@Override
	public Sound transform (Sound input) {
		return PitchSoundTransformation.pitch (input, this.percent);
	}


	private static Sound pitch (Sound sound, float percent) {
		float total = 100;
		if (percent == total){
			return new Sound (sound.getSamples (), sound.getNbBytesPerFrame (), sound.getFreq());
		}
		float nbSamples = sound.getSamples ().length;
		float nbFiltered = percent / total * nbSamples;
		float incr = nbSamples / nbFiltered;
		double [] data = sound.getSamples ();
		double [] ret = new double [(int) (nbFiltered)];
		for (float i = 0; i < incr * nbFiltered; i += incr) {
			int j = (int) (i / incr);
			if (j < ret.length) {
				ret [j] = data [(int) i];
			}
		}
		return new FilteredSound (ret, (int)incr, sound.getNbBytesPerFrame (), sound.getFreq());
	}
}
