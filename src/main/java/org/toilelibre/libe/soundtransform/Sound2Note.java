package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.SimpleNote;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SoundTransformation;

public class Sound2Note {

	public static Note convert (Sound [] channels) {
		Sound channel1 = channels [0];

		int attack = 0;
		int decay = Sound2Note.findDecay (channel1, attack);
		int sustain = Sound2Note.findSustain (channel1, decay);
		int release = Sound2Note.findRelease (channel1);

		return new SimpleNote (channels, Sound2Note.findFrequency (channel1.toSubSound(sustain, release)), attack, decay, sustain, release);

	}

	public static double [] getSoundLoudestFreqs (final double [] magnitude, final Sound sound, final int threshold) {

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
				magnitude [index++] += Sound2Note.computeLoudestFreq (fs, (int) maxFrequency);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length, maxFrequency);
			}
		};
		magnFreqTransform.transform (sound);
		return magnitude;
	}

	private static int findFrequency (Sound channel1) {
		final int threshold = 100;
		double sum = 0;
		int nb = 0;
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];

		Sound2Note.getSoundLoudestFreqs (magnitude, channel1, threshold);

		for (int i = 0; i < magnitude.length; i++) {
			if (magnitude [i] != 0) {
				sum += magnitude [i];
				nb++;
			}
		}
		return (int) (sum / nb);
	}

	private static int findSustain (Sound channel1, int decay) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int sustainIndex = decay;

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

		magnitudeTransform.transform (channel1);

		try {
			MathArrays.checkOrder (Arrays.copyOfRange (magnitude, decay / threshold, magnitude.length), MathArrays.OrderDirection.DECREASING, true);
		} catch (NonMonotonicSequenceException nmse) {
			sustainIndex = (nmse.getIndex () - 1) * threshold;
		}
		return sustainIndex;
	}

	private static int findDecay (Sound channel1, int attack) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int decayIndex = attack;

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

		magnitudeTransform.transform (channel1);

		try {
			MathArrays.checkOrder (Arrays.copyOfRange (magnitude, attack, magnitude.length), MathArrays.OrderDirection.INCREASING, true);
		} catch (NonMonotonicSequenceException nmse) {
			decayIndex = (nmse.getIndex () - 1) * threshold;
		}
		return decayIndex;
	}

	private static int findRelease (Sound channel1) {
		final int threshold = 100;
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
			releaseIndexFromReversed = (nmse.getIndex () - 1) * threshold;
		}
		return channel1.getSamples ().length - releaseIndexFromReversed;
	}

	protected static int computeMagnitude (FrequenciesState fs) {
		double sum = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			sum += fs.getState () [i].abs ();
		}
		return (int) (sum / fs.getState ().length);
	}

	protected static double computeLoudestFreq (FrequenciesState fs, int maxFrequency) {
		double max = 0;
		double freq = 0;
		for (int i = 0; i < maxFrequency / 2; i++) {
			double val = fs.getState () [i].abs ();
			freq = (max < val ? i : freq);
			max = (max < val ? val : max);
		}
		return freq;
	}
}
