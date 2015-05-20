package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 *
 * Set a frequency volume to 0 if the volume is below a threshold
 *
 */
public class ReduceNoiseSoundTransform extends SimpleFrequencySoundTransform<Complex []> {

    private static final float LOW_BOUND = 0;
    private static final float HIGH_BOUND = 100;
    private static final float BYTE_NB_VALUES = 1 << 8;
    
    public enum ReduceNoiseSoundTransformErrorCode implements ErrorCode {

        NOT_A_PERCENT_VALUE ("Not a percent value : %1f");

        private final String messageFormat;

        ReduceNoiseSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }
    private float percentOfMaxVolumeThreshold;

    /**
     * Default constructor. 
     *
     * @param percentOfMaxVolumeThreshold1
     *            percent of max volume threshold (between 0 and 100%)
     * @throws SoundTransformException if the percentOfMaxVolumeThreshold param is not between 0 and 100%
     */
    public ReduceNoiseSoundTransform (final float percentOfMaxVolumeThreshold1) throws SoundTransformException {
        super ();
        this.percentOfMaxVolumeThreshold = this.checkPercent(percentOfMaxVolumeThreshold1);
    }

    private float checkPercent (final float percentOfMaxVolumeThreshold1) throws SoundTransformException {
        if (percentOfMaxVolumeThreshold1 < ReduceNoiseSoundTransform.LOW_BOUND || percentOfMaxVolumeThreshold1 > ReduceNoiseSoundTransform.HIGH_BOUND) {
            throw new SoundTransformException (ReduceNoiseSoundTransformErrorCode.NOT_A_PERCENT_VALUE, new OutOfRangeException (percentOfMaxVolumeThreshold1,
                    ReduceNoiseSoundTransform.LOW_BOUND, ReduceNoiseSoundTransform.HIGH_BOUND), percentOfMaxVolumeThreshold1);
        }
        return percentOfMaxVolumeThreshold1;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset, final int powOf2NearestLength, final int length) {

        final double threshold = this.getMaxAbs (fs.getSampleSize ());
        final Complex [] newAmpl = new Complex [powOf2NearestLength];
        for (double j = 0 ; j < length ; j++) {
            newAmpl [(int) j] = fs.getState () [(int) j].multiply (this.oneIfTrueElseZero (fs.getState () [(int) j].abs () > threshold));
        }
        for (int j = length ; j < powOf2NearestLength ; j++) {
            newAmpl [j] = new Complex (0, 0);
        }
        return new Spectrum<Complex []> (newAmpl, new FormatInfo (fs.getSampleSize (), fs.getSampleRate ()));
    }

    private double getMaxAbs (final int sampleSize) {
        return Math.pow (ReduceNoiseSoundTransform.BYTE_NB_VALUES, sampleSize + 1) * this.percentOfMaxVolumeThreshold / ReduceNoiseSoundTransform.HIGH_BOUND;
    }

    private float oneIfTrueElseZero (final boolean condition) {
        return condition ? 1 : 0;
    }
}
