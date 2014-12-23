package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.util.Arrays;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;

public class MagnitudeADSRHelper implements ADSRHelper {

	public int findSustain (Sound channel1, int decay) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int sustainIndex = decay;

		SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public Sound initSound (Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public double getLowThreshold (double defaultValue) {
				return threshold;
			}

			@Override
			public Spectrum transformFrequencies (Spectrum fs) {
				magnitude [arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
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

	public int findDecay (Sound channel1, int attack) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int decayIndex = attack;

		SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public Sound initSound (Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public double getLowThreshold (double defaultValue) {
				return threshold;
			}

			@Override
			public Spectrum transformFrequencies (Spectrum fs) {
				magnitude [arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
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

	public int findRelease (Sound channel1) {
		final int threshold = 100;
		Sound reversed = new ReverseSoundTransformation ().transform (channel1);
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int releaseIndexFromReversed = 0;

		SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public Sound initSound (Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public double getLowThreshold (double defaultValue) {
				return threshold;
			}

			@Override
			public Spectrum transformFrequencies (Spectrum fs) {
				magnitude [arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
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

	public int computeMagnitude (Spectrum fs) {
		double sum = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			sum += fs.getState () [i].abs ();
		}
		return (int) (sum / fs.getState ().length);
	}
}
