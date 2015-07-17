package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.PianoFrequency;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

/**
 * Peak find algorithm using the Maximum Likelihood method : sums k values each
 * t step. When the k values are at their max, then t is near t0. Finally, f0 is
 * 1 / t0
 *
 * Useful to get the f0 values of a sound (loudest freqs array).
 *
 * As this Peak find algorithm is processed in the time domain rather than the
 * frequency domain, the getDetectedNoteVolume will return an arbitrary, not
 * reliable value.
 **/
public class MaximumLikelihoodSoundTransform extends AbstractLogAware<MaximumLikelihoodSoundTransform> implements PeakFindSoundTransform<Serializable, AbstractLogAware<MaximumLikelihoodSoundTransform>> {

    private static final double A_HUNDRED_PERCENT = 100.0;

    static class WeightIntegralFunction {

        private final Channel input;
        private final int     startSample;
        private final int     endSample;
        private final int     length;

        public WeightIntegralFunction (final Channel input1, final int startSample1, final int endSample1) {
            this.input = input1;
            this.startSample = startSample1;
            this.endSample = endSample1;
            this.length = this.endSample - this.startSample;
        }

        public double value (final double choosenPeriod) {
            final int choosenPeriodsInSignal = (int) (this.length / choosenPeriod);
            final int moduloAfterLastPeriod = (int) (this.length % choosenPeriod);
            return this.sumOfRanges (choosenPeriodsInSignal + 1, (int) choosenPeriod, 0, moduloAfterLastPeriod) + this.sumOfRanges (choosenPeriodsInSignal, (int) choosenPeriod, moduloAfterLastPeriod, choosenPeriod);
        }

        private double sumOfRanges (final int n, final int t, final int startOfInterval, final double endOfInterval) {
            final SignalSumFunction sumFunction = new SignalSumFunction (this.input, t, this.startSample, this.endSample);
            double sum = 0;
            for (int k = startOfInterval ; k <= endOfInterval ; k++) {
                final double valueOfK = sumFunction.value (k);
                sum += valueOfK * valueOfK;
            }
            return sum * n;
        }

    }

    static class SignalSumFunction {

        private final int     choosenPeriod;
        private final int     moduloAfterLastPeriod;
        private final Channel input;
        private final int     choosenPeriodsInSignal;
        private final int     startSample;
        private final int     endSample;
        private final int     length;

        public SignalSumFunction (final Channel input1, final int choosenPeriod1, final int startSample1, final int endSample1) {
            this.input = input1;
            this.choosenPeriod = choosenPeriod1;
            this.startSample = startSample1;
            this.endSample = endSample1;
            this.length = this.endSample - this.startSample;
            this.choosenPeriodsInSignal = this.length / this.choosenPeriod;
            this.moduloAfterLastPeriod = this.length % this.choosenPeriod;
        }

        public double value (final double t) {
            return t < this.moduloAfterLastPeriod ? this.sumOfNPlusOne (t) : this.sumOfN (t);
        }

        private double sumOfN (final double t) {
            return SignalSumFunction.sumImplementation (this.input, this.choosenPeriodsInSignal, this.choosenPeriod, (int) t);
        }

        private static double sumImplementation (final Channel input, final int n, final int p, final int t) {
            double sum = 0;
            for (int k = 0 ; k < n ; k++) {
                final int index = t + k * p;
                sum += index < input.getSamplesLength () ? Math.abs (input.getSampleAt (index)) : 0;
            }
            return sum * 1.0 / n;
        }

        private double sumOfNPlusOne (final double t) {
            return SignalSumFunction.sumImplementation (this.input, this.choosenPeriodsInSignal + 1, this.choosenPeriod, (int) (t + this.startSample));
        }

    }

    private static final float DEFAULT_NOTE_VOLUME_UNKNOWN_VALUE = 40;

    private final int          step;
    private final int          window;
    private final int          minFreq;
    private final int          maxFreq;

    /**
     * Default Constructor
     *
     * @param window1
     *            the samples window length picked at each iteration. This param
     *            can be equal to the sample rate
     * @param step1
     *            the iteration step value (increasing the value will speed the
     *            transform but will be less precise)
     * @param minFreq1
     *            the detection will start with this value as the lowest
     *            possible detected frequency. It is advised not to choose 0 to
     *            avoid detecting bad freqs in a noisy sound
     * @param maxFreq1
     *            the detection will start with this value as the highest
     *            possible detected frequency
     */
    public MaximumLikelihoodSoundTransform (final int window1, final int step1, final int minFreq1, final int maxFreq1) {
        super ();
        this.step = step1;
        this.window = window1;
        this.minFreq = minFreq1;
        this.maxFreq = maxFreq1;
    }

    @Override
    public float [] transform (final Channel input) throws SoundTransformException {
        final Channel windowedInput = new BlackmanHarrisWindowSoundTransform ().transformWholeChannel (input);
        final float [] loudestFreqs = new float [input.getSamplesLength () / this.step + 1];
        for (int momentOfTheSound = 0 ; momentOfTheSound < input.getSamplesLength () ; momentOfTheSound += this.step) {
            final int percent = (int) Math.floor (MaximumLikelihoodSoundTransform.A_HUNDRED_PERCENT * (momentOfTheSound * 1.0 / this.step) / (input.getSamplesLength () * 1.0 / this.step));
            if (percent > Math.floor (MaximumLikelihoodSoundTransform.A_HUNDRED_PERCENT * ((momentOfTheSound - this.step) * 1.0 / this.step) / (input.getSamplesLength () * 1.0 / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, momentOfTheSound / this.step, (int) Math.ceil (input.getSamplesLength () * 1.0 / this.step), percent));
            }
            this.transformMoment (windowedInput, momentOfTheSound, Math.min (momentOfTheSound + this.window, input.getSamplesLength () - 1), loudestFreqs);
        }
        return loudestFreqs;
    }

    private void transformMoment (final Channel input, final int startSample, final int endSample, final float [] loudestFreqs) {
        float foundPeak = 0;
        double foundValue = 0;
        for (final PianoFrequency.PianoValues freq : PianoFrequency.PianoValues.values ()) {
            if (freq.getFrequency () > this.maxFreq || freq.getFrequency () < this.minFreq) {
                continue;
            }
            final float period = 1.0f / freq.getFrequency ();
            final double sum = this.computeSum (input, startSample, endSample, this.periodToLength (input, period));
            if (sum > foundValue) {
                foundPeak = freq.getFrequency ();
                foundValue = sum;
            }
        }
        loudestFreqs [(int) (startSample * 1.0 / this.step)] = foundPeak;
    }

    private int periodToLength (final Channel input, final float period) {
        return (int) (period * input.getSampleRate ());
    }

    private double computeSum (final Channel input, final int startSample, final int endSample, final int choosenPeriod) {
        return new WeightIntegralFunction (input, startSample, endSample).value (choosenPeriod);
    }

    @Override
    public float getDetectedNoteVolume () {
        return MaximumLikelihoodSoundTransform.DEFAULT_NOTE_VOLUME_UNKNOWN_VALUE;
    }

}
