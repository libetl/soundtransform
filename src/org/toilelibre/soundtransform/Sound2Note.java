package org.toilelibre.soundtransform;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Note;
import org.toilelibre.soundtransform.objects.SimpleNote;
import org.toilelibre.soundtransform.objects.Sound;
import org.toilelibre.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.soundtransform.transforms.SoundTransformation;

public class Sound2Note {

	public static Note convert (Sound [] channels) {
		Sound channel1 = channels [0];

		int attack = 0;
		int decay = Sound2Note.findDecay (channel1, attack);
		int sustain = Sound2Note.findSustain (channel1, decay);
		int release = Sound2Note.findRelease (channel1);

		return new SimpleNote (channels, Sound2Note.findFrequency (channel1), attack, decay, sustain, release);

	}

	private static int findFrequency (Sound channel1) {
		final int threshold = channel1.getFreq () / 10;
		double sum = 0;
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];

		SoundTransformation magnFreqTransform = new NoOpFrequencySoundTransformation () {

			int	index	= 0;

			@Override
			public Sound initSound (Sound input) {
				return super.initSound (input);
			}

			@Override
			protected double getLowThreshold (double defaultValue) {
				return threshold;
			}

			@Override
			public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxFrequency) {
				index++;
				magnitude [index] += Sound2Note.computeLoudestFreq (fs);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length, maxFrequency);
			}
		};

		magnFreqTransform.transform (channel1);

		for (int i = 0; i < magnitude.length; i++) {
			sum += magnitude [i];
		}
		return (int) (sum / magnitude.length);
	}

	private static int findSustain (Sound channel1, int decay) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int findDecay (Sound channel1, int attack) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int findRelease (Sound channel1) {
		final int threshold = channel1.getFreq () / 10;
		Sound reversed = new ReverseSoundTransformation ().transform (channel1);
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int releaseIndexFromReversed = 0;

		SoundTransformation magnitudeTransform = new NoOpFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public Sound initSound (Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			protected double getLowThreshold (double defaultValue) {
				return threshold;
			}

			@Override
			public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxFrequency) {
				magnitude [arraylength++] = Sound2Note.computeMagnitude (fs);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length, maxFrequency);
			}

		};

		magnitudeTransform.transform (reversed);

		try {
			MathArrays.checkOrder (magnitude, MathArrays.OrderDirection.INCREASING, true);
		} catch (NonMonotonicSequenceException nmse) {
			releaseIndexFromReversed = nmse.getIndex () * threshold;
		}
		return magnitude.length - releaseIndexFromReversed;
	}

	protected static int computeMagnitude (FrequenciesState fs) {
		double sum = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			sum += fs.getState () [i].abs ();
		}
		return (int) (sum / fs.getState ().length);
	}

	protected static double computeLoudestFreq (FrequenciesState fs) {
		double max = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			double val = fs.getState () [i].abs ();
			max = (max < val ? val : max);
		}
		return max;
	}
}
