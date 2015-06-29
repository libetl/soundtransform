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

    @Override
    public Channel reverse (final Spectrum<Complex []> spectrum) {
        return this.reverse (spectrum, null);
    }

    public Channel reverse (final Spectrum<Complex []> spectrum, final long [] output) {
        return this.reverse (spectrum, output, 0);
    }

    public Channel reverse (final Spectrum<Complex []> spectrum, final long [] output1, final int startOffset) {
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        final Complex [] complexArray = fastFourierTransformer.transform (spectrum.getState (), TransformType.INVERSE);
        long [] output = output1;
        if (output == null) {
            output = new long [complexArray.length];
        }
        for (int i = 0 ; i < complexArray.length ; i++) {
            final int index = i + startOffset;
            if (index < output.length && output [index] == 0) {
                output [index] = (long) Math.floor (complexArray [i].getReal ());
            }
        }
        return new Channel (output, spectrum.getFormatInfo (), 0);
    }

    @Override
    public Channel transform (final AbstractFrequencySoundTransform<Complex []> targetSoundTransform, final Channel sound) {
        final Channel output = targetSoundTransform.initSound (sound);
        // double [] is mandatory to pass it to the common math method
        final double [] transformeddata = new double [targetSoundTransform.getWindowLength (sound.getSampleRate ())];
        for (int i = 0 ; i < sound.getSamplesLength () ; i += targetSoundTransform.getStep (sound.getSampleRate ())) {
            this.stepInto (targetSoundTransform, sound, output.getSamples (), transformeddata, i);
        }
        return output;
    }

    private void stepInto (final AbstractFrequencySoundTransform<Complex []> targetSoundTransform, final Channel sound, final long [] newdata, final double [] transformeddata, int i) {
        final double step = targetSoundTransform.getStep (sound.getSampleRate ());
        final long [] data = sound.getSamples ();
        final int maxlength = targetSoundTransform.getWindowLength (sound.getSampleRate ());
        final int iterationLength = Math.min (maxlength, data.length - i);
        final double amplitude = this.writeTransformedDataAndReturnAmplitude (targetSoundTransform.getWindowTransform (), transformeddata, data, i, (int) step, iterationLength);
        final float volumeInDb = (float) (CommonsMath3FourierTransformHelper.COEFFICIENT * Math.log10 (amplitude));
        final Spectrum<Complex []> spectrum = this.forwardPartOfTheSound (sound, transformeddata);
        final Spectrum<Complex []> result = targetSoundTransform.transformFrequencies (spectrum, i, maxlength, iterationLength, volumeInDb);
        if (result == null) {
            return;
        }
        if (targetSoundTransform.isReverseNecessary ()) {
            this.reverse (result, newdata, i + targetSoundTransform.getOffsetFromASimpleLoop (i, sound.getSampleRate ()));
        }
    }

    private double writeTransformedDataAndReturnAmplitude (final AbstractWindowSoundTransform windowSoundTransform, final double [] transformeddata, final long [] data, final int i, final int step, final int iterationLength) {
        long maxValue = 0;
        long minValue = Long.MAX_VALUE;
        for (int j = i ; j < i + iterationLength ; j++) {
            if (j - i < step) {
                // maxValue and minValue are used to detect if the current
                // transformed sample
                // is a sound or not
                if (maxValue < data [j]) {
                    maxValue = data [j];
                }
                if (minValue > data [j]) {
                    minValue = data [j];
                }
            }
            transformeddata [j - i] = windowSoundTransform.transform ((j - i) / (iterationLength - 1.0)) * data [j];
        }
        return Math.abs (maxValue - minValue);
    }
}
