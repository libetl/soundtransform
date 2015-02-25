package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class LoopSoundTransformation implements SoundTransformation {

    enum LoopSoundTransformationErrorCode implements ErrorCode {
        NOT_POSITIVE_VALUE ("The specified length is not positive (%1d)");

        private String messageFormat;

        LoopSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final int length;

    public LoopSoundTransformation (final int length1) {
        this.length = length1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        final Sound result = new Sound (new long [this.length], input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());

        if (this.length < 0) {
            throw new SoundTransformException (LoopSoundTransformationErrorCode.NOT_POSITIVE_VALUE, new IllegalArgumentException (), 0, this.length);
        }

        for (int i = 0 ; i < this.length ; i++) {
            result.setSampleAt (i, input.getSampleAt (i % input.getSamplesLength ()));
        }
        return result;
    }

}
