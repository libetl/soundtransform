package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

/**
 * Produces an exact copy of the input sound
 *
 */
public class NoOpSoundTransform implements SoundTransform<Channel, Channel> {

    public NoOpSoundTransform () {
    }

    private Channel noop (final Channel sound) {
        final long [] data = sound.getSamples ();

        // same array in newdata
        final long [] newdata = new long [data.length];

        System.arraycopy (data, 0, newdata, 0, data.length);

        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        return this.noop (input);
    }
}
