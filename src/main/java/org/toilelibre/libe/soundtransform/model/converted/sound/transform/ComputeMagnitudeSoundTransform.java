package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Stores an array to know the volume at each step of the sound
 *
 */
public class ComputeMagnitudeSoundTransform implements SoundTransform<Channel, double []> {
    static class ComputeMagnitudeFrequenciesSoundTransform extends SimpleFrequencySoundTransform<Serializable> {

        private int          arraylength;
        private double []    magnitude;
        private final double step;

        /**
         * Default constructor
         *
         * @param step1
         *            iteration step value
         */
        public ComputeMagnitudeFrequenciesSoundTransform (final double step1) {
            super ();
            this.step = step1;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform#getStep(double)
         */
        @Override
        public double getStep (final double defaultValue) {
            return this.step;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform
         * #initSound(org.toilelibre.libe.soundtransform
         * .model.converted.sound.Sound)
         */
        @Override
        public Channel initSound (final Channel input) {
            this.arraylength = 0;
            this.magnitude = new double [(int) (input.getSamplesLength () / this.step + 1)];
            return super.initSound (input);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform#transformFrequencies(double[][],
         * float)
         */
        @Override
        public void transformFrequencies (final double [][] spectrumAsDoubles, final float sampleRate) {
            this.magnitude [this.arraylength++] = this.computeMagnitude (spectrumAsDoubles);
        }

        /**
         * @return the magnitude
         */
        public double [] getMagnitude () {
            return this.magnitude;
        }

        public int computeMagnitude (final double [][] spectrumAsDoubles) {
            double sum = 0;
            for (int i = 0 ; i < spectrumAsDoubles [0].length ; i++) {
                sum += Math.sqrt (spectrumAsDoubles [0] [i] * spectrumAsDoubles [0] [i] + spectrumAsDoubles [1] [i] * spectrumAsDoubles [1] [i]);
            }
            return (int) (sum / spectrumAsDoubles [0].length);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform#isReverseNecessary()
         */
        @Override
        public boolean isReverseNecessary () {
            return false;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform#rawSpectrumPrefered()
         */
        @Override
        public boolean rawSpectrumPrefered () {
            return true;
        }

    }

    private final ComputeMagnitudeFrequenciesSoundTransform decoratedSoundTransform;

    public ComputeMagnitudeSoundTransform (final int step) {
        this.decoratedSoundTransform = new ComputeMagnitudeFrequenciesSoundTransform (step);
    }

    @Override
    public double [] transform (final Channel input) throws SoundTransformException {
        this.decoratedSoundTransform.transform (input);
        return this.decoratedSoundTransform.getMagnitude ();
    }
}
