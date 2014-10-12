package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.util.List;

import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

public class CallHPSFrequencyHelper implements FrequencyHelper {

	@Override
	public int findFrequency (Sound channel1) {
		double sum = 0;
		int nb = 0;

		PeakFindWithHPSSoundTransformation peak = new PeakFindWithHPSSoundTransformation (true);
		peak.transform (channel1);
		List<Integer> magnitude = peak.getLoudestFreqs ();

		for (int i = 0; i < magnitude.size (); i++) {
			sum += magnitude.get (i).intValue ();
			nb++;
		}
		return (int) (sum / nb);
	}
}
