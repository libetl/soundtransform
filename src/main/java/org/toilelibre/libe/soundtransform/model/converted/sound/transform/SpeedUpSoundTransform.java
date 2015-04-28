package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

/**
 * Builds a new sound, shorter than the input, without shifting the frequencies
 * 
 * @param <T>
 *            The kind of object held inside a spectrum.
 */
public class SpeedUpSoundTransform<T extends Serializable> extends SimpleFrequencySoundTransform<T> {

    public enum SpeedUpSoundTransformEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "SpeedUpSoundTransform : Iteration #%1d/%2d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        SpeedUpSoundTransformEventCode (final LogLevel ll, final String mF) {
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

    private static final int A_HUNDRED = 100;

    private final float      factor;
    private Sound            sound;
    private final int        step;
    private float            writeIfGreaterEqThanFactor;

    /**
     * Default constructor
     * 
     * @param step1
     *            iteration step value
     * @param factor1
     *            factor of compression (e.g. 2 means : twice as short)
     */
    public SpeedUpSoundTransform (final int step1, final float factor1) {
        super ();
        this.factor = factor1;
        this.step = step1;
        this.writeIfGreaterEqThanFactor = 0;
    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return (int) (-i * (this.factor - 1) / this.factor);
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public Sound initSound (final Sound input) {
        final long [] newdata = new long [(int) (input.getSamplesLength () / this.factor)];
        this.sound = new Sound (newdata, input.getFormatInfo (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset) {
        final int total = (int) (this.sound.getSamplesLength () / this.factor);
        final int logStep = total / SpeedUpSoundTransform.A_HUNDRED - total / SpeedUpSoundTransform.A_HUNDRED % this.step;
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (total / SpeedUpSoundTransform.A_HUNDRED != 0 && logStep != 0 && offset % logStep == 0) {
            this.log (new LogEvent (SpeedUpSoundTransformEventCode.ITERATION_IN_PROGRESS, offset, (int) (this.sound.getSamplesLength () * this.factor)));
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
