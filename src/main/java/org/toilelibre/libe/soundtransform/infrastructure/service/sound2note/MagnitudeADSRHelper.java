package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.util.Arrays;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;

public class MagnitudeADSRHelper implements ADSRHelper {

	public int computeMagnitude (final Spectrum fs) {
		double sum = 0;
		for (int i = 0; i < fs.getState ().length; i++) {
			sum += fs.getState () [i].abs ();
		}
		return (int) (sum / fs.getState ().length);
	}

	@Override
    public int findDecay (final Sound channel1, final int attack) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int decayIndex = attack;

		final SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public double getLowThreshold (final double defaultValue) {
				return threshold;
			}

			@Override
			public Sound initSound (final Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public Spectrum transformFrequencies (final Spectrum fs) {
				magnitude [this.arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
			}

		};

		magnitudeTransform.transform (channel1);

		try {
			MathArrays.checkOrder (Arrays.copyOfRange (magnitude, attack, magnitude.length), MathArrays.OrderDirection.INCREASING, true);
		} catch (final NonMonotonicSequenceException nmse) {
			decayIndex = (nmse.getIndex () - 1) * threshold;
		}
		return decayIndex;
	}

	@Override
    public int findRelease (final Sound channel1) {
		final int threshold = 100;
		final Sound reversed = new ReverseSoundTransformation ().transform (channel1);
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int releaseIndexFromReversed = 0;

		final SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public double getLowThreshold (final double defaultValue) {
				return threshold;
			}

			@Override
			public Sound initSound (final Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public Spectrum transformFrequencies (final Spectrum fs) {
				magnitude [this.arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
			}

		};

		magnitudeTransform.transform (reversed);

		try {
			MathArrays.checkOrder (magnitude, MathArrays.OrderDirection.INCREASING, true);
		} catch (final NonMonotonicSequenceException nmse) {
			releaseIndexFromReversed = (nmse.getIndex () - 1) * threshold;
		}
		return channel1.getSamples ().length - releaseIndexFromReversed;
	}

	@Override
    public int findSustain (final Sound channel1, final int decay) {
		final int threshold = 100; //Has to be accurate
		final double [] magnitude = new double [channel1.getSamples ().length / threshold + 1];
		int sustainIndex = decay;

		final SoundTransformation magnitudeTransform = new SimpleFrequencySoundTransformation () {
			int	arraylength	= 0;

			@Override
			public double getLowThreshold (final double defaultValue) {
				return threshold;
			}

			@Override
			public Sound initSound (final Sound input) {
				this.arraylength = 0;
				return super.initSound (input);
			}

			@Override
			public Spectrum transformFrequencies (final Spectrum fs) {
				magnitude [this.arraylength++] = MagnitudeADSRHelper.this.computeMagnitude (fs);
				return super.transformFrequencies (fs);
			}

		};

		magnitudeTransform.transform (channel1);

		try {
			MathArrays.checkOrder (Arrays.copyOfRange (magnitude, decay / threshold, magnitude.length), MathArrays.OrderDirection.DECREASING, true);
		} catch (final NonMonotonicSequenceException nmse) {
			sustainIndex = (nmse.getIndex () - 1) * threshold;
		}
		return sustainIndex;
	}
}
