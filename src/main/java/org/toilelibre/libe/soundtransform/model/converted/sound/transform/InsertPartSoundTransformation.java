package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class InsertPartSoundTransformation implements SoundTransformation {

    public enum InsertPartSoundTransformationErrorCode implements ErrorCode {

        START_INDEX_OUT_OF_BOUNDS("The start index is out of bounds (start index : %1d)"), CHANGE_THE_FORMAT_FIRST("Change the inserted sound format first before this transform (actual insert format : %1s, expected : %2s)"), NOT_AS_MANY_CHANNELS(
                "The insert sound does not have as many channels as the input"), ;

        private final String messageFormat;

        InsertPartSoundTransformationErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private final Sound[] subsound;
    private final int start;

    public InsertPartSoundTransformation(final Sound[] subsound1, final int start1) throws SoundTransformException {
        this.subsound = subsound1.clone();
        this.start = start1;
    }

    private void checks(final Sound input) {
        if (this.start < 0) {
            throw new SoundTransformRuntimeException(InsertPartSoundTransformationErrorCode.START_INDEX_OUT_OF_BOUNDS, new IllegalArgumentException(), this.start);
        }
        if (input.getChannelNum() >= this.subsound.length) {
            throw new SoundTransformRuntimeException(InsertPartSoundTransformationErrorCode.NOT_AS_MANY_CHANNELS, new IllegalArgumentException(), this.start);
        }
        if (!this.subsound[input.getChannelNum()].getFormatInfo().sameFormatAs(input.getFormatInfo())) {
            throw new SoundTransformRuntimeException(InsertPartSoundTransformationErrorCode.CHANGE_THE_FORMAT_FIRST, new IllegalArgumentException(), this.subsound[input.getChannelNum()].getFormatInfo(), input.getFormatInfo());
        }
    }

    private Sound insertIn(final Sound sound) {
        final long[] subsamples = this.subsound[sound.getChannelNum()].getSamples();
        final long[] samples = new long[Math.max(this.start, sound.getSamplesLength()) + subsamples.length];
        System.arraycopy(sound.getSamples(), 0, samples, 0, Math.min(this.start, sound.getSamplesLength()));
        System.arraycopy(subsamples, 0, samples, this.start, subsamples.length);
        if (sound.getSamplesLength() - this.start > 0) {
            System.arraycopy(sound.getSamples(), this.start, samples, this.start + subsamples.length, sound.getSamplesLength() - this.start);
        }
        return new Sound(samples, sound.getFormatInfo(), sound.getChannelNum());
    }

    @Override
    public Sound transform(final Sound input) {
        this.checks(input);
        return this.insertIn(input);
    }
}
