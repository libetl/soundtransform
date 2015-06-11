package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import java.io.Serializable;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.toilelibre.libe.soundtransform.infrastructure.service.freqs.PianoFrequency;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

public class MaximumLikelihoodSoundTransform extends AbstractLogAware<MaximumLikelihoodSoundTransform> implements PeakFindSoundTransform<Serializable, AbstractLogAware<MaximumLikelihoodSoundTransform>> {

    private static final double A_HUNDRED_PERCENT = 100.0;

    static class WeightIntegralFunction implements UnivariateFunction {

        private final double [] inputDoubles;

        public WeightIntegralFunction (final double [] inputDoubles1) {
            this.inputDoubles = inputDoubles1.clone ();
        }

        @Override
        public double value (final double choosenPeriod) {
            final int choosenPeriodsInSignal = (int) (this.inputDoubles.length / choosenPeriod);
            final int moduloAfterLastPeriod = (int) (this.inputDoubles.length % choosenPeriod);
            return this.sumOfRanges (choosenPeriodsInSignal + 1, (int) choosenPeriod, 0, moduloAfterLastPeriod) + this.sumOfRanges (choosenPeriodsInSignal, (int) choosenPeriod, moduloAfterLastPeriod, choosenPeriod);
        }

        private double sumOfRanges (final int n, final int t, final int startOfInterval, final double endOfInterval) {
            final SignalSumFunction sumFunction = new SignalSumFunction (this.inputDoubles, t);
            double sum = 0;
            for (int k = startOfInterval ; k <= endOfInterval ; k++) {
                final double valueOfK = sumFunction.value (k);
                sum += valueOfK * valueOfK;
            }
            return sum * n;
        }

    }

    static class SignalSumFunction implements UnivariateFunction {

        private final int       choosenPeriod;
        private final int       moduloAfterLastPeriod;
        private final double [] inputDoubles;
        private final int       choosenPeriodsInSignal;

        public SignalSumFunction (final double [] inputDoubles1, final int choosenPeriod1) {
            this.inputDoubles = inputDoubles1.clone ();
            this.choosenPeriod = choosenPeriod1;
            this.choosenPeriodsInSignal = this.inputDoubles.length / this.choosenPeriod;
            this.moduloAfterLastPeriod = this.inputDoubles.length % this.choosenPeriod;
        }

        @Override
        public double value (final double t) {
            return t < this.moduloAfterLastPeriod ? this.sumOfNPlusOne (t) : this.sumOfN (t);
        }

        private double sumOfN (final double t) {
            return SignalSumFunction.sumImplementation (this.inputDoubles, this.choosenPeriodsInSignal, this.choosenPeriod, (int) t);
        }

        private static double sumImplementation (final double [] input, final int n, final int p, final int t) {
            double sum = 0;
            for (int k = 0 ; k < n ; k++) {
                final int index = t + k * p;
                sum += index < input.length ? input [index] : 0;
            }
            return sum * 1.0 / n;
        }

        private double sumOfNPlusOne (final double t) {
            return SignalSumFunction.sumImplementation (this.inputDoubles, this.choosenPeriodsInSignal + 1, this.choosenPeriod, (int) t);
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
     * @param step
     *            the iteration step value (increasing the value will speed the
     *            transform but will be less precise)
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
        final float [] loudestFreqs = new float [input.getSamplesLength () / this.step + 1];
        for (int momentOfTheSound = 0 ; momentOfTheSound < input.getSamplesLength () ; momentOfTheSound += this.step) {
            final int percent = (int) Math.floor (MaximumLikelihoodSoundTransform.A_HUNDRED_PERCENT * (momentOfTheSound / this.step) / (input.getSamplesLength () / this.step));
            if (percent > Math.floor (MaximumLikelihoodSoundTransform.A_HUNDRED_PERCENT * ((momentOfTheSound - this.step) / this.step) / (input.getSamplesLength () / this.step))) {
                this.log (new LogEvent (PeakFindSoundTransformEventCode.ITERATION_IN_PROGRESS, momentOfTheSound / this.step, (int) Math.ceil (input.getSamplesLength () / this.step), percent));
            }
            this.transformMoment (input, momentOfTheSound, Math.min (momentOfTheSound + this.window, input.getSamplesLength () - 1), loudestFreqs);
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
        loudestFreqs [startSample / this.step] = foundPeak;
    }

    private int periodToLength (final Channel input, final float period) {
        return (int) (period * input.getSampleRate ());
    }

    private double computeSum (final Channel input, final int startSample, final int endSample, final int choosenPeriod) {
        final double [] samplesAsDoubleArray = this.findSamplesAsDoubleArray (input, startSample, endSample);
        return new WeightIntegralFunction (samplesAsDoubleArray).value (choosenPeriod);
    }

    private double [] findSamplesAsDoubleArray (final Channel input, final int startSample, final int endSample) {
        final double [] samples = new double [endSample - startSample + 1];
        for (int copyIndex = startSample ; copyIndex < endSample ; copyIndex++) {
            samples [copyIndex - startSample] = input.getSampleAt (copyIndex);
        }
        return samples;
    }

    @Override
    public float getDetectedNoteVolume () {
        return MaximumLikelihoodSoundTransform.DEFAULT_NOTE_VOLUME_UNKNOWN_VALUE;
    }

}
