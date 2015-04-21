package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

/**
 * Replaces a part of a sound with another sound
 * The target sound must have the same number of channels as the replacement,
 * and the insert index must not be out of bounds
 *
 */
public class ReplacePartSoundTransformation implements SoundTransformation {

    public enum ReplacePartSoundTransformationErrorCode implements ErrorCode {

        START_INDEX_OUT_OF_BOUNDS ("The start index is out of bounds (start index : %1d)"), CHANGE_THE_FORMAT_FIRST ("Change the replacement sound format first before this transform (actual replacement format : %1s, expected : %2s)"), NOT_AS_MANY_CHANNELS (
                "The replacement sound does not have as many channels as the input"), ;

        private final String messageFormat;

        ReplacePartSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final Sound [] replacement;
    private final int      start;

    /**
     * Default constructor
     * @param replacement1 replacement sound
     * @param start1 start index
     */
    public ReplacePartSoundTransformation (final Sound [] replacement1, final int start1) {
        this.replacement = replacement1.clone ();
        this.start = start1;
    }

    private void checks (final Sound input) {
        if (this.start < 0) {
            throw new SoundTransformRuntimeException (ReplacePartSoundTransformationErrorCode.START_INDEX_OUT_OF_BOUNDS, new IllegalArgumentException (), this.start);
        }
        if (input.getChannelNum () >= this.replacement.length) {
            throw new SoundTransformRuntimeException (ReplacePartSoundTransformationErrorCode.NOT_AS_MANY_CHANNELS, new IllegalArgumentException (), this.start);
        }
        if (!this.replacement [input.getChannelNum ()].getFormatInfo ().sameFormatAs (input.getFormatInfo ())) {
            throw new SoundTransformRuntimeException (ReplacePartSoundTransformationErrorCode.CHANGE_THE_FORMAT_FIRST, new IllegalArgumentException (), this.replacement [input.getChannelNum ()].getFormatInfo (), input.getFormatInfo ());
        }
    }

    private Sound replace (final Sound sound) {
        final long [] samples = new long [Math.max (sound.getSamplesLength (), this.start + this.replacement [sound.getChannelNum ()].getSamplesLength ())];
        System.arraycopy (sound.getSamples (), 0, samples, 0, sound.getSamplesLength ());
        System.arraycopy (this.replacement [sound.getChannelNum ()].getSamples (), 0, samples, this.start, this.replacement [sound.getChannelNum ()].getSamplesLength ());
        return new Sound (samples, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        this.checks (input);
        return this.replace (input);
    }
}
