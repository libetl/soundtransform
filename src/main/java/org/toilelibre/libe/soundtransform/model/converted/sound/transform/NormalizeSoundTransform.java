package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Raises the sound volume to match a certain percentage of the maximum possible
 * level
 *
 */
public class NormalizeSoundTransform implements SoundTransform<Channel, Channel> {

    public enum NormalizeSoundTransformErrorCode implements ErrorCode {

        COEFFICIENT_IS_ABOVE_ONE ("The coefficient of the normalizer is above one"), COEFFICIENT_IS_BELOW_ZERO ("The coefficient of the normalizer is below zero");

        private final String messageFormat;

        NormalizeSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final double NB_BYTE_VALUES = 1 << Byte.SIZE;
    private final float         coefficient;

    /**
     * Default constructor
     *
     * @param coefficient1
     *            coefficient of the max level (0 &lt;= coefficient &lt;= 1)
     * @throws SoundTransformException
     *             The coefficient of the normalizer is above one or below zero
     */
    public NormalizeSoundTransform (final float coefficient1) throws SoundTransformException {
        this.coefficient = this.checkCoefficient (coefficient1);
    }

    private float checkCoefficient (final float coefficient1) throws SoundTransformException {
        if (coefficient1 > 1) {
            throw new SoundTransformException (NormalizeSoundTransformErrorCode.COEFFICIENT_IS_ABOVE_ONE, new IllegalArgumentException ());
        }
        if (coefficient1 < 0) {
            throw new SoundTransformException (NormalizeSoundTransformErrorCode.COEFFICIENT_IS_BELOW_ZERO, new IllegalArgumentException ());
        }
        return coefficient1;
    }

    private Channel normalize (final Channel sound) {
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
        final double maxValue = Math.pow (NormalizeSoundTransform.NB_BYTE_VALUES, sound.getSampleSize ()) - 1;
        final double ratio = maxValue / max;
        for (int i = 0 ; i < data.length ; i++) {
            final double rescaled = data [i] * ratio * this.coefficient;
            newdata [i] = (long) Math.floor (rescaled);
        }

        // normalized result in newdata
        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        return this.normalize (input);
    }
}
