package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.toilelibre.soundtransform.note.Note;
import org.toilelibre.soundtransform.note.SimpleNote;
import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Sound;
import org.toilelibre.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.soundtransform.transforms.SoundTransformation;

public class Sound2NoteTest {
    private ClassLoader classLoader = Thread.currentThread()
            .getContextClassLoader();
    private File input = new File(classLoader.getResource("piano_a.wav")
            .getFile());

    @Test
    public Note run() throws UnsupportedAudioFileException, IOException {

        AudioInputStream ais = AudioFileHelper.getAudioInputStream(input);
        TransformSound ts = new TransformSound();

        Sound[] channels = ts.fromInputStream(ais);
        Sound channel1 = channels[0];

        int attack = 0;
        int decay = this.findDecay(channel1, attack);
        int sustain = this.findSustain(channel1, decay);
        int release = this.findRelease(channel1);

        return new SimpleNote(channels, this.findFrequency(channel1), attack,
                decay, sustain, release);

    }

    private int findFrequency(Sound channel1) {
        final int threshold = 4410;
        double sum = 0;
        final double[] magnitude = new double[channel1.getSamples().length
                / threshold + 1];

        SoundTransformation magnFreqTransform = new NoOpFrequencySoundTransformation() {

            int index = 0;

            @Override
            public Sound initSound(Sound input) {
                return super.initSound(input);
            }

            @Override
            protected double getLowThreshold(double defaultValue) {
                return threshold;
            }

            @Override
            public FrequenciesState transformFrequencies(FrequenciesState fs,
                    int offset, int powOf2NearestLength, int length,
                    double maxFrequency) {
                index++;
                magnitude[index] += Sound2NoteTest.this.computeLoudestFreq(fs);
                return super.transformFrequencies(fs, offset,
                        powOf2NearestLength, length, maxFrequency);
            }
        };

        magnFreqTransform.transform(channel1);

        for (int i = 0; i < magnitude.length; i++) {
            sum += magnitude[i];
        }
        return (int) (sum / magnitude.length);
    }

    private int findSustain(Sound channel1, int decay) {
        // TODO Auto-generated method stub
        return 0;
    }

    private int findDecay(Sound channel1, int decay) {
        // TODO Auto-generated method stub
        return 0;
    }

    private int findRelease(Sound channel1) {
        final int threshold = 4410;
        Sound reversed = new ReverseSoundTransformation().transform(channel1);
        final double[] magnitude = new double[channel1.getSamples().length
                / threshold + 1];
        int releaseIndexFromReversed = 0;

        SoundTransformation magnitudeTransform = new NoOpFrequencySoundTransformation() {
            int arraylength = 0;

            @Override
            public Sound initSound(Sound input) {
                this.arraylength = 0;
                return super.initSound(input);
            }

            @Override
            protected double getLowThreshold(double defaultValue) {
                return threshold;
            }

            @Override
            public FrequenciesState transformFrequencies(FrequenciesState fs,
                    int offset, int powOf2NearestLength, int length,
                    double maxFrequency) {
                magnitude[arraylength++] = Sound2NoteTest.this
                        .computeMagnitude(fs);
                return super.transformFrequencies(fs, offset,
                        powOf2NearestLength, length, maxFrequency);
            }

        };

        magnitudeTransform.transform(reversed);
        try {
            MathArrays.checkOrder(magnitude,
                    MathArrays.OrderDirection.INCREASING, true);
        } catch (NonMonotonicSequenceException nmse) {
            releaseIndexFromReversed = nmse.getIndex() * threshold;
            System.out.println(releaseIndexFromReversed + " "
                    + magnitude[nmse.getIndex() - 1] + " "
                    + magnitude[nmse.getIndex()]);
            System.out.println(Arrays.toString(magnitude).substring(0, 150));
        }

        return magnitude.length - releaseIndexFromReversed;
    }

    protected int computeMagnitude(FrequenciesState fs) {
        double sum = 0;
        for (int i = 0; i < fs.getState().length; i++) {
            sum += fs.getState()[i].abs();
        }
        return (int) (sum / fs.getState().length);
    }

    protected double computeLoudestFreq(FrequenciesState fs) {
        double max = 0;
        for (int i = 0; i < fs.getState().length; i++) {
            double val = fs.getState()[i].abs();
            max = (max < val ? val : max);
        }
        return max;
    }
}
