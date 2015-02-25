package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SpeedUpSoundTransformation<T> extends SimpleFrequencySoundTransformation<T> {

    public enum SpeedUpSoundTransformationEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "SpeedUpSoundTransformation : Iteration #%1d/%2d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        SpeedUpSoundTransformationEventCode (final LogLevel ll, final String mF) {
            this.logLevel = ll;
            this.messageFormat = mF;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final float factor;
    private Sound       sound;
    private final int   step;
    private float       writeIfGreaterEqThanFactor;

    public SpeedUpSoundTransformation (final int step1, final float factor) {
        super ();
        this.factor = factor;
        this.step = step1;
        this.writeIfGreaterEqThanFactor = 0;
    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return (int) ((-i * (this.factor - 1)) / this.factor);
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public Sound initSound (final Sound input) {
        final long [] newdata = new long [(int) (input.getSamplesLength () / this.factor)];
        this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset) {
        final int total = (int) (this.sound.getSamplesLength () / this.factor);
        final int logStep = (total / 100) - ((total / 100) % this.step);
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (((total / 100) != 0) && (logStep != 0) && ((offset % logStep) == 0)) {
            this.log (new LogEvent (SpeedUpSoundTransformationEventCode.ITERATION_IN_PROGRESS, offset, (int) (this.sound.getSamplesLength () * this.factor)));
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
