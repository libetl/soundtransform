package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Removes a part of a sound
 *
 * The result of the method contains the rest of the sound, and the removed
 * interval is not available from here.
 */
public class CutSoundTransform implements SoundTransform<Channel, Channel> {

    enum SoundCutSoundTransformErrorCode implements ErrorCode {
        INDEXS_OUT_OF_BOUND ("The specified indexes are out of bound (maximum : %1d -> %2d , actual : %3d -> %4d)");

        private String messageFormat;

        SoundCutSoundTransformErrorCode (final String mF) {
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
     * Default Constructor
     *
     * @param start1
     *            start of the interval
     * @param end1
     *            end of the interval
     */
    public CutSoundTransform (final int start1, final int end1) {
        this.start = start1;
        this.end = end1;
    }

    @Override
    public Channel transform (final Channel input) throws SoundTransformException {
        final int delta = this.end - this.start;
        final int newlength = input.getSamplesLength () - delta;
        if (this.start > this.end || this.start < 0 || this.end >= input.getSamplesLength ()) {
            throw new SoundTransformException (SoundCutSoundTransformErrorCode.INDEXS_OUT_OF_BOUND, new IllegalArgumentException (), 0, input.getSamplesLength (), this.start, this.end);
        }
        final Channel result = new Channel (new long [newlength], input.getFormatInfo (), input.getChannelNum ());

        for (int i = 0 ; i < this.start ; i++) {
            result.setSampleAt (i, input.getSampleAt (i));
        }
        for (int i = this.end ; i < input.getSamplesLength () ; i++) {
            result.setSampleAt (i - delta, input.getSampleAt (i));
        }
        return result;
    }

}
