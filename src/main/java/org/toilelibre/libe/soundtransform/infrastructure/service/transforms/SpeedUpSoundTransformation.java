package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SpeedUpSoundTransformation extends SimpleFrequencySoundTransformation {

    private final float factor;
    private Sound       sound;
    private final int   threshold;
    private float       writeIfGreaterEqThanFactor;

    public SpeedUpSoundTransformation (FourierTransformHelper helper1, final int threshold, final float factor) {
        super (helper1);
        this.factor = factor;
        this.threshold = threshold;
        this.writeIfGreaterEqThanFactor = 0;
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return (int) (-i * (this.factor - 1) / this.factor);
    }

    @Override
    public Sound initSound (final Sound input) {
        final long [] newdata = new long [(int) (input.getSamples ().length / this.factor)];
        this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum transformFrequencies (final Spectrum fs, final int offset) {
        final int total = (int) (this.sound.getSamples ().length / this.factor);
        final int logStep = total / 100 - total / 100 % this.threshold;
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (total / 100 != 0 && logStep != 0 && offset % logStep == 0) {
            this.log (new LogEvent (LogLevel.VERBOSE, "SpeedUpSoundTransformation : Iteration #" + offset + "/" + (int) (this.sound.getSamples ().length * this.factor)));
        }
        if (this.writeIfGreaterEqThanFactor >= this.factor) {
            this.writeIfGreaterEqThanFactor -= this.factor;
            return fs;
        } else {
            this.writeIfGreaterEqThanFactor++;
            return null;
        }
    }

}
