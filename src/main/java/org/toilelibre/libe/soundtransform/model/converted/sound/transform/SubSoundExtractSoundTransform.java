package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Cuts a part of a sound and returns it. The rest of the sound will not be
 * available.
 *
 */
public class SubSoundExtractSoundTransform implements SoundTransform<Sound, Sound> {
    enum SubSoundExtractSoundTransformErrorCode implements ErrorCode {
        INDEXS_OUT_OF_BOUND ("The specified indexes are out of bound (maximum : %1d -> %2d , actual : %3d -> %4d)");

        private String messageFormat;

        SubSoundExtractSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final int start;
    private final int end;

    /**
     * Default constructor
     * 
     * @param start1
     *            start index
     * @param end1
     *            end index
     */
    public SubSoundExtractSoundTransform (final int start1, final int end1) {
        this.start = start1;
        this.end = end1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        final Sound result = new Sound (new long [this.end - this.start], input.getFormatInfo (), input.getChannelNum ());

        if (this.start > this.end || this.start < 0 || this.end >= input.getSamplesLength ()) {
            throw new SoundTransformException (SubSoundExtractSoundTransformErrorCode.INDEXS_OUT_OF_BOUND, new IllegalArgumentException (), 0, input.getSamplesLength (), this.start, this.end);
        }

        for (int i = this.start ; i < this.end ; i++) {
            result.setSampleAt (i - this.start, input.getSampleAt (i));
        }
        return result;
    }

}
