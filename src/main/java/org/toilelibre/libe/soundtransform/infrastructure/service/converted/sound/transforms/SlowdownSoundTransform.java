package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

/**
 * Builds a new sound, longer than the input, without shifting the frequencies
 */
public class SlowdownSoundTransform extends SimpleFrequencySoundTransform<Complex []> {

    public enum SlowdownSoundTransformErrorCode implements ErrorCode {

        WINDOW_LENGTH_IS_LOWER_THAN_TWICE_THE_STEP ("Window length is lower than twice the step value (%1i < 2 * %2i)"), WINDOW_LENGTH_IS_NOT_A_POWER_OF_2 ("Window length is not a power of 2 (%1i)");

        private final String messageFormat;

        SlowdownSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum SlowdownSoundTransformEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "SlowdownSoundTransform : Iteration #%1d/%2d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        SlowdownSoundTransformEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
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

    private static final int TWICE     = 2;

    private static final int A_HUNDRED = 100;

    private final float      factor;
    private Channel          sound;
    private final int        step;
    private float            writeIfGreaterEqThan1;
    private int              additionalFrames;
    private final int        windowLength;

    /**
     * Default constructor WARN : can fail for various reasons
     *
     * @param step1
     *            must be > that the f0 of the sound. Else it will not fail but
     *            will produce a bad sound
     * @param factor1
     *            the slowdown factor
     * @param windowLength1
     *            must be a power of 2 and must be >= 2 * step
     * @throws SoundTransformException
     *             if the constraint about the windowLength is not met
     */
    public SlowdownSoundTransform (final int step1, final float factor1, final int windowLength1) throws SoundTransformException {
        super ();
        this.factor = factor1;
        this.step = step1;
        this.writeIfGreaterEqThan1 = 0;
        this.additionalFrames = 0;
        this.windowLength = windowLength1;
        this.checkConstructor ();
    }

    private void checkConstructor () throws SoundTransformException {
        if (this.windowLength < SlowdownSoundTransform.TWICE * this.step) {
            throw new SoundTransformException (SlowdownSoundTransformErrorCode.WINDOW_LENGTH_IS_LOWER_THAN_TWICE_THE_STEP, new IllegalArgumentException (), this.windowLength, this.step);
        }
        if ((this.windowLength & -this.windowLength) != this.windowLength) {
            throw new SoundTransformException (SlowdownSoundTransformErrorCode.WINDOW_LENGTH_IS_NOT_A_POWER_OF_2, new IllegalArgumentException (), this.windowLength);
        }
    }

    private void copyBeginingOfSpectrumToFillTheGaps (final Spectrum<Complex []> fs, final int start, final int end) {
        Complex [] complexArray = fs.getState ();
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);
        for (int i = start ; i < end ; i++) {
            if (i < this.sound.getSamplesLength () && i - start < complexArray.length) {
                this.sound.setSampleAt (i, (long) complexArray [i - start].getReal ());
            }
        }

    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return this.additionalFrames * this.step;
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.windowLength == 0) {
            return super.getWindowLength (freqmax);
        }
        return this.windowLength;
    }

    @Override
    public Channel initSound (final Channel input) {
        final long [] newdata = new long [(int) (input.getSamplesLength () * this.factor)];
        this.sound = new Channel (newdata, input.getFormatInfo (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset) {
        final int total = (int) (this.sound.getSamplesLength () * this.factor);
        final int logStep = total / SlowdownSoundTransform.A_HUNDRED - total / SlowdownSoundTransform.A_HUNDRED % this.step;
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (total / SlowdownSoundTransform.A_HUNDRED != 0 && logStep != 0 && offset % logStep == 0) {
            this.log (new LogEvent (SlowdownSoundTransformEventCode.ITERATION_IN_PROGRESS, offset, (int) (this.sound.getSamplesLength () / this.factor)));
        }
        final float remaining = (float) (this.factor - Math.floor (this.factor));
        final int padding = (int) Math.floor (this.writeIfGreaterEqThan1 + remaining);
        final int loops = (int) (this.factor + padding - 1);
        this.additionalFrames += loops;
        final int start = offset + Math.max (0, this.getOffsetFromASimpleLoop (0, 0) - loops * this.step);
        final int end = offset + this.getOffsetFromASimpleLoop (0, 0);
        this.copyBeginingOfSpectrumToFillTheGaps (fs, start, end);
        if (padding == 1) {
            this.writeIfGreaterEqThan1 -= 1;
        } else {
            this.writeIfGreaterEqThan1 += remaining;
        }
        return fs;
    }

}
