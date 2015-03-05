package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class NormalizeSoundTransformation implements SoundTransformation {

    public enum NormalizeSoundTransformationErrorCode implements ErrorCode {

        COEFFICIENT_IS_ABOVE_ONE ("The coefficient of the normalizer is above one"), COEFFICIENT_IS_BELOW_ZERO ("The coefficient of the normalizer is below zero");

        private final String messageFormat;

        NormalizeSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final double NB_BYTE_VALUES = 1 << Byte.SIZE;
    private final float         coefficient;

    public NormalizeSoundTransformation (final float coefficient1) throws SoundTransformException {
        this.coefficient = this.checkCoefficient (coefficient1);
    }

    private float checkCoefficient (float coefficient1) throws SoundTransformException {
        if (coefficient1 > 1) {
            throw new SoundTransformException (NormalizeSoundTransformationErrorCode.COEFFICIENT_IS_ABOVE_ONE, new IllegalArgumentException ());
        }
        if (coefficient1 < 0) {
            throw new SoundTransformException (NormalizeSoundTransformationErrorCode.COEFFICIENT_IS_BELOW_ZERO, new IllegalArgumentException ());
        }
        return coefficient1;
    }

    private Sound normalize (final Sound sound) {
        final long [] data = sound.getSamples ();
        final long [] newdata = new long [sound.getSamplesLength ()];
        // this is the raw audio data -- no header

        // find the max:
        double max = 0;
        for (final long element : data) {
            if (Math.abs (element) > max) {
                max = Math.abs (element);
            }
        }

        // now find the result, with scaling:
        final double maxValue = Math.pow (NormalizeSoundTransformation.NB_BYTE_VALUES, sound.getSampleSize ()) - 1;
        final double ratio = maxValue / max;
        for (int i = 0 ; i < data.length ; i++) {
            final double rescaled = data [i] * ratio * this.coefficient;
            newdata [i] = (long) Math.floor (rescaled);
        }

        // normalized result in newdata
        return new Sound (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.normalize (input);
    }
}
