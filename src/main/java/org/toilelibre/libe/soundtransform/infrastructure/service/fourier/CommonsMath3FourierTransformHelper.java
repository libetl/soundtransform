package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.AbstractFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class CommonsMath3FourierTransformHelper implements FourierTransformHelper {

    @Override
    public Sound transform (final AbstractFrequencySoundTransformation st, final Sound sound) {
        final Sound output = st.initSound (sound);
        final double freqmax = sound.getSampleRate ();
        final double threshold = st.getLowThreshold (freqmax);
        final int maxlength = st.getWindowLength (freqmax);
        final long [] data = sound.getSamples ();
        final long [] newdata = output.getSamples ();
        final double [] transformeddata = new double [maxlength];
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        for (int i = 0; i < data.length; i += threshold) {
            long maxValue = 0;
            long minValue = Long.MAX_VALUE;
            final int length = Math.min (maxlength, data.length - i);
            for (int j = i; j < i + length; j++) {
                if (j - i < threshold){
                    //maxValue and minValue are used to detect if the current transformed sample
                    //is a sound or not
                    if (maxValue < data [j]){
                        maxValue = data [j];
                    }
                    if (minValue > data [j]){
                        minValue = data [j];
                    }
                }
                transformeddata [j - i] = data [j];
            }
            Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);

            final Spectrum fs = new Spectrum (complexArray, (int) freqmax, sound.getNbBytesPerSample ());
            final Spectrum result = st.transformFrequencies (fs, i, maxlength, length,
            		(float)(10.0f * Math.log10 (Math.abs (maxValue - minValue))));
            if (result == null) {
                continue;
            }
            complexArray = fastFourierTransformer.transform (result.getState (), TransformType.INVERSE);
            final int k = st.getOffsetFromASimpleLoop (i, freqmax);
            for (int j = 0; j < freqmax; j++) {
                if (i + j + k < newdata.length && newdata [i + j + k] == 0) {
                    newdata [i + j + k] = (long) Math.floor (complexArray [j].getReal ());
                }
            }
        }
        return output;
    }
}
