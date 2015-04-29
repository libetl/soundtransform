package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Repeats a sound as another sound
 *
 */
public class LoopSoundTransform implements SoundTransform<Channel, Channel> {

    enum LoopSoundTransformErrorCode implements ErrorCode {
        NOT_POSITIVE_VALUE ("The specified length is not positive (%1d)");

        private String messageFormat;

        LoopSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final int length;

    /**
     * Default constructor
     *
     * @param length1
     *            length (in samples) of the repetition(s)
     */
    public LoopSoundTransform (final int length1) {
        this.length = length1;
    }

    @Override
    public Channel transform (final Channel input) throws SoundTransformException {
        final Channel result = new Channel (new long [this.length], input.getFormatInfo (), input.getChannelNum ());

        if (this.length <= 0) {
            throw new SoundTransformException (LoopSoundTransformErrorCode.NOT_POSITIVE_VALUE, new IllegalArgumentException (), 0, this.length);
        }

        for (int i = 0 ; i < this.length ; i++) {
            result.setSampleAt (i, input.getSampleAt (i % input.getSamplesLength ()));
        }
        return result;
    }

}
