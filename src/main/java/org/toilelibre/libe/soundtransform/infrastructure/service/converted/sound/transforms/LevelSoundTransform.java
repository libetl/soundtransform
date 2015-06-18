package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class LevelSoundTransform implements SoundTransform<Channel, Channel> {

    private static final double NB_BYTE_VALUES = 1 << Byte.SIZE;
    private int step;

    public LevelSoundTransform (final int step1) {
        this.step = step1;
    }
    
    @Override
    public Channel transform (Channel input) throws SoundTransformException {
        double [] magnitudes = new ComputeMagnitudeSoundTransform (this.step).transform (input);

        final long [] data = input.getSamples ();
        final long [] newdata = new long [input.getSamplesLength ()];

        final double maxMagnitude = Math.pow (LevelSoundTransform.NB_BYTE_VALUES, input.getSampleSize ()) - 1;

        // now find the result, with scaling:
        for (int i = 0 ; i < data.length ; i++) {
            int currentMagnitudeIndex = (int) (i * 1.0 / this.step);
            final double rescaled = data [i] / magnitudes [currentMagnitudeIndex] * maxMagnitude;
            newdata [i] = (long) Math.floor (rescaled);
        }

        // normalized result in newdata
        return new Channel (newdata, input.getFormatInfo (), input.getChannelNum ());
    }

}
