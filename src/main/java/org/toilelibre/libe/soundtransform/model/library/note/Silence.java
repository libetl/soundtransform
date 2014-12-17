package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class Silence implements Note {

	private static final int SAMPLE_RATE = 48000;
	@Override
    public String getName () {
	    return "SILENCE";
    }

    private Sound generateSilence (int frequency, int channelnum, float lengthInSeconds) {
		int nbSamples = (int)(lengthInSeconds * lengthInSeconds);
	    return new Sound (new long [nbSamples], 2, Silence.SAMPLE_RATE, 1);
	}
	
	@Override
    public Sound getAttack (int frequency, int channelnum, float lengthInSeconds) {
		return this.generateSilence (frequency, channelnum, lengthInSeconds);
    }

	@Override
    public Sound getDecay (int frequency, int channelnum, float lengthInSeconds) {
		return this.generateSilence (frequency, channelnum, lengthInSeconds);
    }

	@Override
    public Sound getSustain (int frequency, int channelnum, float lengthInSeconds) {
		return this.generateSilence (frequency, channelnum, lengthInSeconds);
    }

	@Override
    public Sound getRelease (int frequency, int channelnum, float lengthInSeconds) {
		return this.generateSilence (frequency, channelnum, lengthInSeconds);
    }

	@Override
    public int getFrequency () {
		return 0;
    }

}
