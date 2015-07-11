package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

/**
 * Inserts a sound into another
 *
 */
public class InsertPartSoundTransform implements SoundTransform<Channel, Channel> {

    public enum InsertPartSoundTransformErrorCode implements ErrorCode {

        START_INDEX_OUT_OF_BOUNDS ("The start index is out of bounds (start index : %1d)"), CHANGE_THE_FORMAT_FIRST ("Change the inserted sound format first before this transform (actual insert format : %1s, expected : %2s)"), NOT_AS_MANY_CHANNELS (
                "The insert sound does not have as many channels as the input"), ;

        private final String messageFormat;

        InsertPartSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final Channel [] subsound;
    private final int        start;

    /**
     * Default constructor
     *
     * @param subsound1
     *            the sound to insert (only one sound is allowed, each element
     *            is a sound channel)
     * @param start1
     *            start index where to insert the sound
     */
    public InsertPartSoundTransform (final Sound subsound1, final int start1) {
        this.subsound = subsound1.getChannels ();
        this.start = start1;
    }

    private void checks (final Channel input) {
        if (this.start < 0) {
            throw new SoundTransformRuntimeException (InsertPartSoundTransformErrorCode.START_INDEX_OUT_OF_BOUNDS, new IllegalArgumentException (), this.start);
        }
        if (input.getChannelNum () >= this.subsound.length) {
            throw new SoundTransformRuntimeException (InsertPartSoundTransformErrorCode.NOT_AS_MANY_CHANNELS, new IllegalArgumentException (), this.start);
        }
        if (!this.subsound [input.getChannelNum ()].getFormatInfo ().sameFormatAs (input.getFormatInfo ())) {
            throw new SoundTransformRuntimeException (InsertPartSoundTransformErrorCode.CHANGE_THE_FORMAT_FIRST, new IllegalArgumentException (), this.subsound [input.getChannelNum ()].getFormatInfo (), input.getFormatInfo ());
        }
    }

    private Channel insertIn (final Channel sound) {
        final long [] samples = new long [Math.max (this.start, sound.getSamplesLength ()) + this.subsound [sound.getChannelNum ()].getSamplesLength ()];
        sound.copyTo (samples, 0, 0, Math.min (this.start, sound.getSamplesLength ()));
        this.subsound [sound.getChannelNum ()].copyTo (samples, 0, this.start, this.subsound [sound.getChannelNum ()].getSamplesLength ());
        if (sound.getSamplesLength () - this.start > 0) {
            sound.copyTo (samples, this.start, this.start + this.subsound [sound.getChannelNum ()].getSamplesLength (), sound.getSamplesLength () - this.start);
        }
        return new Channel (samples, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        this.checks (input);
        return this.insertIn (input);
    }
}
