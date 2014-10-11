package org.toilelibre.libe.soundtransform.pda;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.objects.Spectrum;
import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.SimpleNote;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.PeakFindSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SoundTransformation;

public class Sound2Note {

	public static Note convert (String fileName, Sound [] channels) {
		return Sound2Note.convert (fileName, channels, Sound2Note.findFrequency (channels [0]));
	}

	public static Note convert (String fileName, Sound [] channels, int frequency) {
		Sound channel1 = channels [0];

		int attack = 0;
		int decay = Sound2Note.findDecay (channel1, attack);
		int sustain = Sound2Note.findSustain (channel1, decay);
		int release = Sound2Note.findRelease (channel1);

		return new SimpleNote (fileName, channels, frequency, attack, decay, sustain, release);

	}

	private static int findFrequency (Sound channel1) {
		double sum = 0;
		int nb = 0;

		PeakFindSoundTransformation peak = new PeakFindSoundTransformation (true);
		peak.transform (channel1);
		List<Integer> magnitude = peak.getLoudestFreqs ();

		for (int i = 0; i < magnitude.size (); i++) {
			sum += magnitude.get (i).intValue ();
			nb++;
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
			public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {
				magnitude [arraylength++] = Sound2Note.computeMagnitude (fs);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length);
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
			public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {
				magnitude [arraylength++] = Sound2Note.computeMagnitude (fs);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length);
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
			public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {
				magnitude [arraylength++] = Sound2Note.computeMagnitude (fs);
				return super.transformFrequencies (fs, offset, powOf2NearestLength, length);
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

	protected static int computeMagnitude (Spectrum fs) {
		double sum = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			sum += fs.getState () [i].abs ();
		}
		return (int) (sum / fs.getState ().length);
	}

	protected static double computeLoudestFreq (Spectrum fs) {
		double max = 0;
		double freq = 0;
		for (int i = 0; i < fs.getSampleRate () / 2; i++) {
			double val = Math.pow (fs.getState () [i].abs (), 2);
			freq = (max < val ? i : freq);
			max = (max < val ? val : max);
		}
		return freq;
	}
}
