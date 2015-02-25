package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.AbstractFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class CommonsMath3FourierTransformHelper implements FourierTransformHelper<Complex []> {

    private static final float COEFFICIENT = 10.0f;

    private Spectrum<Complex []> forwardPartOfTheSound (final Sound sound, final double [] transformeddata) {
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        final Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
        return new Spectrum<Complex []> (complexArray, sound.getSampleRate (), sound.getNbBytesPerSample ());
    }

    @Override
    public Sound reverse (final Spectrum<Complex []> spectrum) {
        return this.reverse (spectrum, null);
    }

    public Sound reverse (final Spectrum<Complex []> spectrum, final long [] output) {
        return this.reverse (spectrum, output, 0);
    }

    public Sound reverse (final Spectrum<Complex []> spectrum, final long [] output1, final int startOffset) {
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
        return new Sound (output, spectrum.getNbBytes (), spectrum.getSampleRate (), 0);
    }

    @Override
    public Sound transform (final AbstractFrequencySoundTransformation<Complex []> st, final Sound sound) {
        final Sound output = st.initSound (sound);
        final double sampleRate = sound.getSampleRate ();
        final double step = st.getStep (sampleRate);
        final int maxlength = st.getWindowLength (sampleRate);
        final long [] data = sound.getSamples ();
        final long [] newdata = output.getSamples ();
        // double [] is mandatory to pass it to the common math method
        final double [] transformeddata = new double [maxlength];
        for (int i = 0 ; i < data.length ; i += step) {
            final int iterationLength = Math.min (maxlength, data.length - i);
            final double amplitude = this.writeTransformedDataAndReturnAmplitude (transformeddata, data, i, (int) step, iterationLength);
            final float volumeInDb = (float) (CommonsMath3FourierTransformHelper.COEFFICIENT * Math.log10 (amplitude));
            final Spectrum<Complex []> spectrum = this.forwardPartOfTheSound (sound, transformeddata);
            final Spectrum<Complex []> result = st.transformFrequencies (spectrum, i, maxlength, iterationLength, volumeInDb);
            if (result == null) {
                continue;
            }
            this.reverse (result, newdata, i + st.getOffsetFromASimpleLoop (i, sampleRate));
        }
        return output;
    }

    private double writeTransformedDataAndReturnAmplitude (final double [] transformeddata, final long [] data, final int i, final int step, final int iterationLength) {
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
            transformeddata [j - i] = data [j];
        }
        return Math.abs (maxValue - minValue);
    }
}
