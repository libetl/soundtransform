package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SlowdownSoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {

    public enum SlowdownSoundTransformationErrorCode implements ErrorCode {

        WINDOW_LENGTH_IS_LOWER_THAN_TWICE_THE_THRESHOLD ("Window length is lower than twice the threshold (%1i < 2 * %2i)"), WINDOW_LENGTH_IS_NOT_A_POWER_OF_2 ("Window length is not a power of 2 (%1i)");

        private final String messageFormat;

        SlowdownSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum SlowdownSoundTransformationEventCode implements EventCode {

        ITERATION_IN_PROGRESS (LogLevel.VERBOSE, "SlowdownSoundTransformation : Iteration #%1d/%2d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        SlowdownSoundTransformationEventCode (final LogLevel ll, final String mF) {
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

    private final float factor;
    private Sound       sound;
    private final int   threshold;
    private float       writeIfGreaterEqThan1;
    private int         additionalFrames;
    private final int   windowLength;

    /**
     * WARN : can fail for various reasons
     *
     * @param threshold
     *            must be > that the f0 of the sound. Else it will not fail but
     *            will produce a bad sound
     * @param factor
     *            the slowdown factor
     * @param windowLength
     *            must be a power of 2 and must be >= 2 * threshold
     * @throws SoundTransformException
     *             if the constraint about the windowLength is not met
     */
    public SlowdownSoundTransformation (final int threshold, final float factor, final int windowLength) throws SoundTransformException {
        super ();
        this.factor = factor;
        this.threshold = threshold;
        this.writeIfGreaterEqThan1 = 0;
        this.additionalFrames = 0;
        this.windowLength = windowLength;
        this.checkConstructor ();
    }

    private void checkConstructor () throws SoundTransformException {
        if (this.windowLength < (2 * this.threshold)) {
            throw new SoundTransformException (SlowdownSoundTransformationErrorCode.WINDOW_LENGTH_IS_LOWER_THAN_TWICE_THE_THRESHOLD, new IllegalArgumentException (), this.windowLength, this.threshold);
        }
        if ((this.windowLength & -this.windowLength) != this.windowLength) {
            throw new SoundTransformException (SlowdownSoundTransformationErrorCode.WINDOW_LENGTH_IS_NOT_A_POWER_OF_2, new IllegalArgumentException (), this.windowLength);
        }
    }

    private void copyBeginingOfSpectrumToFillTheGaps (final Spectrum<Complex []> fs, final int start, final int end) {
        Complex [] complexArray = fs.getState ();
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);
        for (int i = start ; i < end ; i++) {
            if ((i < this.sound.getSamples ().length) && ((i - start) < complexArray.length)) {
                this.sound.getSamples () [i] = (long) complexArray [i - start].getReal ();
            }
        }

    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    @Override
    public int getOffsetFromASimpleLoop (final int i, final double step) {
        return this.additionalFrames * this.threshold;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.windowLength == 0) {
            return super.getWindowLength (freqmax);
        }
        return this.windowLength;
    }

    @Override
    public Sound initSound (final Sound input) {
        final long [] newdata = new long [(int) (input.getSamples ().length * this.factor)];
        this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset) {
        final int total = (int) (this.sound.getSamples ().length * this.factor);
        final int logStep = (total / 100) - ((total / 100) % this.threshold);
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (((total / 100) != 0) && (logStep != 0) && ((offset % logStep) == 0)) {
            this.log (new LogEvent (SlowdownSoundTransformationEventCode.ITERATION_IN_PROGRESS, offset, (int) (this.sound.getSamples ().length / this.factor)));
        }
        final float remaining = (float) (this.factor - Math.floor (this.factor));
        final int padding = (int) Math.floor (this.writeIfGreaterEqThan1 + remaining);
        final int loops = (int) ((this.factor + padding) - 1);
        this.additionalFrames += loops;
        final int start = offset + Math.max (0, this.getOffsetFromASimpleLoop (0, 0) - (loops * this.threshold));
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
