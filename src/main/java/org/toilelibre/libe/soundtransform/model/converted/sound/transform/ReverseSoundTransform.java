package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

/**
 * Make a new sound such as newsoundsamples [i] = soundsamples [length - i]
 *
 */
public class ReverseSoundTransform implements SoundTransform<Channel, Channel> {

    /**
     * Default constructor
     */
    public ReverseSoundTransform () {
    }

    private Channel reverse (final Channel sound) {
        final long [] data = sound.getSamples ();
        final long [] newdata = new long [sound.getSamplesLength ()];
        // this is the raw audio data -- no header

        for (int i = 0 ; i < data.length ; i++) {
            newdata [i] = data [data.length - i - 1];
        }
        // normalized result in newdata
        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        return this.reverse (input);
    }
}
