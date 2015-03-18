package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class FadeSoundTransformation implements SoundTransformation {

    public enum FadeSoundTransformationErrorCode implements ErrorCode {

        FADE_LENGTH_LONGER_THAN_SOUND("The fade length is longer than the sound itself (length : %1d, fade length : %2d)"), FADE_LENGTH_IS_BELOW_ZERO("The expected fade length is less than 0");

        private final String messageFormat;

        FadeSoundTransformationErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private final int length;
    private final boolean fadeIn;

    public FadeSoundTransformation(final int length1, final boolean fadeIn1) throws SoundTransformException {
        this.length = this.checkLength(length1);
        this.fadeIn = fadeIn1;
    }

    private int checkLength(final int length1) throws SoundTransformException {
        if (length1 < 0) {
            throw new SoundTransformException(FadeSoundTransformationErrorCode.FADE_LENGTH_IS_BELOW_ZERO, new IllegalArgumentException());
        }
        return length1;
    }

    private Sound fade(final Sound sound) {
        final long[] data = sound.getSamples();
        final long[] newdata = data.clone();

        for (int i = 0; i < this.length; i++) {
            final int realIndex = this.fadeIn ? i : sound.getSamplesLength() - i - 1;
            final float ratio = i * 1.0f / this.length;
            newdata[realIndex] = (long) (data[realIndex] * ratio);
        }

        return new Sound(newdata, sound.getFormatInfo(), sound.getChannelNum());
    }

    @Override
    public Sound transform(final Sound input) {
        if (this.length > input.getSamplesLength()) {
            throw new SoundTransformRuntimeException(FadeSoundTransformationErrorCode.FADE_LENGTH_LONGER_THAN_SOUND, new IllegalArgumentException(), this.length, input.getSamplesLength());
        }
        return this.fade(input);
    }
}
