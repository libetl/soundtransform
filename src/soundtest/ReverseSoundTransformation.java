package soundtest;

public class ReverseSoundTransformation implements SoundTransformation {
	
	public ReverseSoundTransformation () {
    }


	@Override
	public Sound transform (Sound input) {
		return ReverseSoundTransformation.reverse (input);
	}

	private static Sound reverse (Sound sound) {
		double [] data = sound.getSamples ();
		double [] newdata = new double [sound.getSamples ().length];
		// this is the raw audio data -- no header

		for (int i = 0; i < data.length; i++) {
			newdata [i] = data [data.length - i - 1];
		}
		// normalized result in newdata
		return new Sound (newdata, sound.getNbBytesPerFrame ());
	}
}
