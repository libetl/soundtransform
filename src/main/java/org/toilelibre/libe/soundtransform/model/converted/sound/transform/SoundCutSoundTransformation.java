package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SoundCutSoundTransformation implements SoundTransformation {

    enum SoundCutSoundTransformationErrorCode implements ErrorCode {
        INDEXS_OUT_OF_BOUND ("The specified indexs are out of bound (maximum : %1d -> %2d , actual : %3d -> %4d)");

        private String messageFormat;

        SoundCutSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final int start;
    private final int end;

    public SoundCutSoundTransformation (final int start1, final int end1) {
        this.start = start1;
        this.end = end1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        final int delta = this.end - this.start;
        final int newlength = input.getSamples ().length - delta;
        final Sound result = new Sound (new long [newlength], input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());

        if (this.start > this.end || this.start < 0 || this.end >= input.getSamples ().length) {
            throw new SoundTransformException (SoundCutSoundTransformationErrorCode.INDEXS_OUT_OF_BOUND, new IllegalArgumentException (), 0, input.getSamples ().length, this.start, this.end);
        }

        for (int i = 0 ; i < this.start ; i++) {
            result.getSamples () [i] = input.getSamples () [i];
        }
        for (int i = this.end ; i < input.getSamples ().length ; i++) {
            result.getSamples () [i - delta] = input.getSamples () [i];
        }
        return result;
    }

}
