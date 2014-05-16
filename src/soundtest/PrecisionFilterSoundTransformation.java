package soundtest;

public class PrecisionFilterSoundTransformation implements SoundTransformation {

	private int percent = 20;
	
	public PrecisionFilterSoundTransformation (int percent) {
		this.percent = percent;
    }


	@Override
	public Sound transform (Sound input) {
		return PrecisionFilterSoundTransformation.precisionFilter (input, this.percent);
	}


	private static Sound precisionFilter (Sound sound, float percent) {
		float total = 100;
		if (percent == total){
			return new Sound (sound.getSamples (), sound.getNbBytesPerFrame ());
		}
		float nbSamples = sound.getSamples ().length;
		float nbFiltered = percent / total * nbSamples;
		float incr = nbSamples / nbFiltered;
		double [] data = sound.getSamples ();
		double [] ret = new double [(int) (nbFiltered)];
		double [] time = new double [(int) (nbFiltered)];
		for (float i = 0; i < incr * nbFiltered; i += incr) {
			int j = (int) (i / incr);
			if (j < ret.length) {
				ret [j] = data [(int) i];
				time [j] = i;
			}
		}
		return new FilteredSound (ret, time, sound.getNbBytesPerFrame ());
	}
}
