package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

/**
 * Fade in / Fade out operation of a sound. Ability to change the first part of
 * a sound as an intro or the last part as an outro (the sound volume gradually
 * increases in the intro and gradually descreases in the outro)
 *
 */
public class FadeSoundTransform implements SoundTransform<Channel, Channel> {

    public enum FadeSoundTransformErrorCode implements ErrorCode {

        FADE_LENGTH_LONGER_THAN_SOUND ("The fade length is longer than the sound itself (length : %1d, fade length : %2d)"), FADE_LENGTH_IS_BELOW_ZERO ("The expected fade length is less than 0");

        private final String messageFormat;

        FadeSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final int     length;
    private final boolean fadeIn;

    /**
     * Default constructor
     * 
     * @param length1
     *            length of the fade
     * @param fadeIn1
     *            true for fadeIn, false for fadeOut
     * @throws SoundTransformException
     *             The fade length is longer than the sound itself
     */
    public FadeSoundTransform (final int length1, final boolean fadeIn1) throws SoundTransformException {
        this.length = this.checkLength (length1);
        this.fadeIn = fadeIn1;
    }

    private int checkLength (final int length1) throws SoundTransformException {
        if (length1 < 0) {
            throw new SoundTransformException (FadeSoundTransformErrorCode.FADE_LENGTH_IS_BELOW_ZERO, new IllegalArgumentException ());
        }
        return length1;
    }

    private Channel fade (final Channel sound) {
        final long [] data = sound.getSamples ();
        final long [] newdata = data.clone ();

        for (int i = 0 ; i < this.length ; i++) {
            final int realIndex = this.fadeIn ? i : sound.getSamplesLength () - i - 1;
            final float ratio = i * 1.0f / this.length;
            newdata [realIndex] = (long) (data [realIndex] * ratio);
        }

        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        if (this.length > input.getSamplesLength ()) {
            throw new SoundTransformRuntimeException (FadeSoundTransformErrorCode.FADE_LENGTH_LONGER_THAN_SOUND, new IllegalArgumentException (), this.length, input.getSamplesLength ());
        }
        return this.fade (input);
    }
}
