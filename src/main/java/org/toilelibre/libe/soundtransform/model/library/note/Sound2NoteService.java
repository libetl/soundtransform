package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class Sound2NoteService {

	public static Note convert (final String fileName, final Sound [] channels) {
		return Sound2NoteService.convert (fileName, channels, Sound2NoteService.frequencyHelper.findFrequency (channels [0]));
	}
	public static Note convert (final String fileName, final Sound [] channels, final int frequency) {
		final Sound channel1 = channels [0];

		final int attack = 0;
		final int decay = Sound2NoteService.adsrHelper.findDecay (channel1, attack);
		final int sustain = Sound2NoteService.adsrHelper.findSustain (channel1, decay);
		final int release = Sound2NoteService.adsrHelper.findRelease (channel1);

		return new SimpleNote (fileName, channels, frequency, attack, decay, sustain, release);

	}

	private static ADSRHelper	   adsrHelper	    = new org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.MagnitudeADSRHelper ();

	private static FrequencyHelper	frequencyHelper	= new org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.CallHPSFrequencyHelper ();
}
