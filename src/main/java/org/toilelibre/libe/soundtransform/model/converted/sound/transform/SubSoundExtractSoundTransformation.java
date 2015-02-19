package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SubSoundExtractSoundTransformation implements SoundTransformation {
    enum SubSoundExtractSoundTransformationErrorCode implements ErrorCode {
        INDEXS_OUT_OF_BOUND ("The specified indexs are out of bound (maximum : %1d -> %2d , actual : %3d -> %4d)");

        private String messageFormat;

        SubSoundExtractSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final int start;
    private final int end;

    public SubSoundExtractSoundTransformation (int start1, int end1) {
        this.start = start1;
        this.end = end1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        final Sound result = new Sound (new long [this.end - this.start], input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());

        if ((this.start > this.end) || (this.start < 0) || (this.end >= input.getSamples ().length)) {
            throw new SoundTransformException (SubSoundExtractSoundTransformationErrorCode.INDEXS_OUT_OF_BOUND, new IllegalArgumentException (), 0, input.getSamples ().length, this.start, this.end);
        }

        for (int i = this.start ; i < this.end ; i++) {
            result.getSamples () [i - this.start] = input.getSamples () [i];
        }
        return result;
    }

}
