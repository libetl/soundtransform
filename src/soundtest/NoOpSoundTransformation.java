package soundtest;

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
		return new Sound (data, sound.getNbBytesPerFrame (), sound.getFreq());
	}
}
