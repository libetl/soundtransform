package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public abstract class AbstractWindowSoundTransform implements SoundTransform<Channel, Channel> {

    
    @Override
    public Channel transform (Channel sound) throws SoundTransformException {

        final long [] data = sound.getSamples ();
        final long [] newdata = new long [sound.getSamplesLength ()];


        // now find the result, with scaling:
        for (int i = 0 ; i < data.length ; i++) {
            final double rescaled = data [i] * this.applyFunction (i, data.length);
            newdata [i] = (long) Math.floor (rescaled);
        }

        // normalized result in newdata
        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    protected abstract double applyFunction (int iteration, int length);

}
