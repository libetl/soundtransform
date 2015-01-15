package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SlowdownSoundTransformation extends SimpleFrequencySoundTransformation {

    private final float factor;
    private Sound       sound;
    private final int   threshold;
    private float       writeIfGreaterEqThan1;
    private int         additionalFrames;

    public SlowdownSoundTransformation (FourierTransformHelper helper1, final int threshold, final float factor) {
        super (helper1);
        this.factor = factor;
        this.threshold = threshold;
        this.writeIfGreaterEqThan1 = 0;
        this.additionalFrames = 0;
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
    public Spectrum transformFrequencies (final Spectrum fs, final int offset) {
        final int total = (int) (this.sound.getSamples ().length * this.factor);
        final int logStep = total / 100 - total / 100 % this.threshold;
        // This if helps to only log some of all iterations to avoid being too
        // verbose
        if (total / 100 != 0 && logStep != 0 && offset % logStep == 0) {
            this.log (new LogEvent (LogLevel.VERBOSE, "SlowdownSoundTransformation : Iteration #" + offset + "/" + (int) (this.sound.getSamples ().length / this.factor)));
        }
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        Complex [] complexArray = fs.getState ();
        final float remaining = (float) (this.factor - Math.floor (this.factor));
        final int padding = (int) Math.floor (this.writeIfGreaterEqThan1 + remaining);
        final int loops = (int) (this.factor + padding - 1);
        this.additionalFrames += loops;
        for (int p = 0 ; p < loops ; p++) {
            complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);

            for (int j = 0 ; j < fs.getSampleRate () ; j++) {
                if (offset + p * fs.getSampleRate () + j < this.sound.getSamples ().length && this.sound.getSamples () [offset + p * fs.getSampleRate () + j] == 0) {
                    this.sound.getSamples () [offset + p * fs.getSampleRate () + j] = (long) Math.floor (complexArray [j].getReal ());
                }
            }
        }
        if (padding == 1) {
            this.writeIfGreaterEqThan1 -= 1;
        } else {
            this.writeIfGreaterEqThan1 += remaining;
        }
        return fs;
    }

}
