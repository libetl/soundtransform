package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SlowdownSoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {

    private final float factor;
    private Sound       sound;
    private final int   threshold;
    private float       writeIfGreaterEqThan1;
    private int         additionalFrames;
    private int         windowLength;

    public SlowdownSoundTransformation (final int threshold, final float factor) {
        super ();
        this.factor = factor;
        this.threshold = threshold;
        this.writeIfGreaterEqThan1 = 0;
        this.additionalFrames = 0;
    }
    
    public SlowdownSoundTransformation (final int threshold, final float factor, final int windowLength) {
        this (threshold, factor);
        this.windowLength = windowLength;
    }

    @Override
    public int getWindowLength (double freqmax) {
        if (this.windowLength == 0){
            return super.getWindowLength (freqmax);
        }
        return this.windowLength;
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
    public Sound initSound (final Sound input) {
        final long [] newdata = new long [(int) (input.getSamples ().length * this.factor)];
        this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
        return this.sound;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset) {
        final int total = (int) (this.sound.getSamples ().length * this.factor);
        final int logStep = total / 100 - total / 100 % this.threshold;
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (total / 100 != 0 && logStep != 0 && offset % logStep == 0) {
            this.log (new LogEvent (LogLevel.VERBOSE, "SlowdownSoundTransformation : Iteration #" + offset + "/" + (int) (this.sound.getSamples ().length / this.factor)));
        }
        final float remaining = (float) (this.factor - Math.floor (this.factor));
        final int padding = (int) Math.floor (this.writeIfGreaterEqThan1 + remaining);
        final int loops = (int) (this.factor + padding - 1);
        this.additionalFrames += loops;
        this.copySpectrumXtimes (fs, loops, offset);
        if (padding == 1) {
            this.writeIfGreaterEqThan1 -= 1;
        } else {
            this.writeIfGreaterEqThan1 += remaining;
        }
        return fs;
    }

    private void copySpectrumXtimes (Spectrum<Complex []> fs, int loops, int offset) {
        Complex [] complexArray = fs.getState ();
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        for (int p = 0 ; p < loops ; p++) {
            complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);

            for (int j = 0 ; j < this.getWindowLength (0) ; j++) {
                if (offset + p * fs.getSampleRate () + j < this.sound.getSamples ().length) {
                    this.sound.getSamples () [offset + p * this.getWindowLength (0) + j] = (long) Math.floor (complexArray [j].getReal ());
                }
            }
        }
        
    }

}
