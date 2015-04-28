package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Stores an array to know the volume at each step of the sound
 *
 */
public class ComputeMagnitudeSoundTransform implements SoundTransform<Sound, double []> {
    class ComputeMagnitudeFrequenciesSoundTransform extends SimpleFrequencySoundTransform<Complex []> {

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
        public Sound initSound (final Sound input) {
            this.arraylength = 0;
            this.magnitude = new double [(int) (input.getSamplesLength () / this.step + 1)];
            return super.initSound (input);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.toilelibre.libe.soundtransform.model.converted.sound.transform
         * .SimpleFrequencySoundTransform
         * #transformFrequencies(org.toilelibre.libe
         * .soundtransform.model.converted.spectrum.Spectrum)
         */
        @Override
        public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs) {
            this.magnitude [this.arraylength++] = this.computeMagnitude (fs);
            return super.transformFrequencies (fs);
        }

        /**
         * @return the magnitude
         */
        public double [] getMagnitude () {
            return this.magnitude;
        }

        public int computeMagnitude (final Spectrum<Complex []> fs) {
            double sum = 0;
            for (int i = 0 ; i < fs.getState ().length ; i++) {
                sum += fs.getState () [i].abs ();
            }
            return (int) (sum / fs.getState ().length);
        }
    }

    private final ComputeMagnitudeFrequenciesSoundTransform decoratedSoundTransform;

    public ComputeMagnitudeSoundTransform (final int step) {
        this.decoratedSoundTransform = new ComputeMagnitudeFrequenciesSoundTransform (step);
    }

    @Override
    public double [] transform (final Sound input) throws SoundTransformException {
        this.decoratedSoundTransform.transform (input);
        return this.decoratedSoundTransform.getMagnitude ();
    }
}
