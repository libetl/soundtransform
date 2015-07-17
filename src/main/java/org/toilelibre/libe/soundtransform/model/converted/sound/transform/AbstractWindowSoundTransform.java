package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

public abstract class AbstractWindowSoundTransform implements SoundTransform<Double, Double> {

    @Override
    /**
     * @param progress is equal to iteration / sound.getSampleslength () - 1
     */
    public Double transform (final Double progress) {

        return this.applyFunction (progress);
    }

    public Channel transformWholeChannel (final Channel sound) {

        final long [] newdata = new long [sound.getSamplesLength ()];

        // now find the result, with scaling:
        for (int i = 0 ; i < sound.getSamplesLength () ; i++) {
            final double rescaled = sound.getSampleAt (i) * this.applyFunction (i * 1.0 / (sound.getSamplesLength () - 1));
            newdata [i] = (long) rescaled;
        }

        // normalized result in newdata
        return new Channel (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    protected abstract double applyFunction (double progress);

}
