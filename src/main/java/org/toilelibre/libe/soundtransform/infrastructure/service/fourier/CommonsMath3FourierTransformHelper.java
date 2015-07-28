package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.AbstractFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.AbstractWindowSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

final class CommonsMath3FourierTransformHelper implements FourierTransformHelper<Complex []> {

    private static final float COEFFICIENT = 10.0f;

    private Spectrum<Complex []> forwardPartOfTheSound (final Channel sound, final double [] transformeddata) {
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        final Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
        return new Spectrum<Complex []> (complexArray, sound.getFormatInfo ());
    }

    private double [][] forwardPartOfTheSoundInPlace (final double [] transformeddata) {
        final double [][] result = new double [] [] { transformeddata, new double [transformeddata.length] };
        FastFourierTransformer.transformInPlace (result, DftNormalization.STANDARD, TransformType.FORWARD);
        return result;
    }

    @Override
    public Channel reverse (final Spectrum<Complex []> spectrum) {
        return this.reverse (spectrum, null);
    }

    public Channel reverse (final Spectrum<Complex []> spectrum, final Channel output) {
        return this.reverse (spectrum, output, 0);
    }

    public Channel reverse (final Spectrum<Complex []> spectrum, final Channel output, final int startOffset) {
        Channel output1 = output;
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        final Complex [] complexArray = fastFourierTransformer.transform (spectrum.getState (), TransformType.INVERSE);

        if (output == null) {
            output1 = new Channel (new long [complexArray.length], spectrum.getFormatInfo (), 0);
        }
        for (int i = 0 ; i < complexArray.length ; i++) {
            final int index = i + startOffset;
            if (index < output1.getSamplesLength () && output1.getSampleAt (index) == 0) {
                output1.setSampleAt (index, (long) Math.floor (complexArray [i].getReal ()));
            }
        }
        return output1;
    }

    @Override
    public Channel transform (final AbstractFrequencySoundTransform<Complex []> targetSoundTransform, final Channel sound) {
        final Channel output = targetSoundTransform.initSound (sound);
        // double [] is mandatory to pass it to the common math method
        final double [] transformeddata = new double [targetSoundTransform.getWindowLength (sound.getSampleRate ())];
        for (int i = 0 ; i < sound.getSamplesLength () ; i += targetSoundTransform.getStep (sound.getSampleRate ())) {
            this.stepInto (targetSoundTransform, sound, output, transformeddata, i);
        }
        return output;
    }

    private void stepInto (final AbstractFrequencySoundTransform<Complex []> targetSoundTransform, final Channel sound, final Channel output, final double [] transformeddata, final int i) {
        final double step = targetSoundTransform.getStep (sound.getSampleRate ());
        final int maxlength = targetSoundTransform.getWindowLength (sound.getSampleRate ());
        final int iterationLength = Math.min (maxlength, sound.getSamplesLength () - i);
        final double amplitude = this.writeTransformedDataAndReturnAmplitude (targetSoundTransform.getWindowTransform (), transformeddata, sound, i, (int) step, iterationLength);
        final float volumeInDb = (float) (CommonsMath3FourierTransformHelper.COEFFICIENT * Math.log10 (amplitude));

        if (targetSoundTransform.rawSpectrumPrefered ()) {
            final double [][] spectrumInDoubles = this.forwardPartOfTheSoundInPlace (transformeddata);
            targetSoundTransform.transformFrequencies (spectrumInDoubles, sound.getSampleRate (), i, maxlength, iterationLength, volumeInDb);
        } else {
            final Spectrum<Complex []> spectrum = this.forwardPartOfTheSound (sound, transformeddata);
            final Spectrum<Complex []> result = targetSoundTransform.transformFrequencies (spectrum, i, maxlength, iterationLength, volumeInDb);

            if (result == null) {
                return;
            }

            if (targetSoundTransform.isReverseNecessary ()) {
                this.reverse (result, output, i + targetSoundTransform.getOffsetFromASimpleLoop (i, sound.getSampleRate ()));
            }
        }
    }

    private double writeTransformedDataAndReturnAmplitude (final AbstractWindowSoundTransform windowSoundTransform, final double [] transformeddata, final Channel channel, final int i, final int step, final int iterationLength) {
        long maxValue = 0;
        long minValue = Long.MAX_VALUE;
        for (int j = i ; j < i + iterationLength ; j++) {
            if (j - i < step) {
                // maxValue and minValue are used to detect if the current
                // transformed sample
                // is a sound or not
                if (maxValue < channel.getSampleAt (j)) {
                    maxValue = channel.getSampleAt (j);
                }
                if (minValue > channel.getSampleAt (j)) {
                    minValue = channel.getSampleAt (j);
                }
            }
            transformeddata [j - i] = windowSoundTransform.transform ((j - i) / (iterationLength - 1.0)) * channel.getSampleAt (j);
        }
        return Math.abs (maxValue - minValue);
    }
}
