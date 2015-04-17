package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

public class PeakFindWithHPSSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> implements PeakFindSoundTransformation<T> {

    private double                  step;
    private List<float []>          allLoudestFreqs;
    private float []                loudestfreqs;
    private boolean                 note;
    private float                   fsLimit;
    private int                     windowLength;
    private int                     soundLength;
    private float                   detectedNoteVolume;

    private final SpectrumHelper<T> spectrumHelper;

    @SuppressWarnings ("unchecked")
    private PeakFindWithHPSSoundTransformation () {
        super ();
        this.allLoudestFreqs = new LinkedList<float []> ();
        this.spectrumHelper = $.select (SpectrumHelper.class);
    }

    public PeakFindWithHPSSoundTransformation (final boolean note1) {
        this ();
        this.note = note1;
        this.step = 100;
        this.windowLength = -1;
        this.soundLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (final double step1) {
        this ();
        this.step = step1;
        this.windowLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (final double step1, final int windowLength1) {
        this ();
        this.step = step1;
        this.windowLength = windowLength1;
    }

    private float bestCandidate (final float [] peaks) {
        int leftEdge = 0;
        while (leftEdge < peaks.length && peaks [leftEdge] <= 30) {
            leftEdge++;
        }
        int rightEdge = leftEdge;
        while (rightEdge < peaks.length && Math.abs ((peaks [rightEdge] - peaks [leftEdge]) * 1.0 / peaks [rightEdge]) * 100.0 < 10) {
            rightEdge++;
        }
        int sum = 0;
        for (int i = leftEdge ; i < rightEdge ; i++) {
            sum += peaks [i];
        }

        return rightEdge == leftEdge ? sum : sum * 1.0f / (rightEdge - leftEdge);
    }

    @Override
    public float getDetectedNoteVolume () {
        return this.detectedNoteVolume;
    }

    @Override
    public float [] getLoudestFreqs () {
        return this.loudestfreqs.clone ();
    }

    @Override
    public List<float []> getAllLoudestFreqs () {
        return this.allLoudestFreqs;
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.windowLength != -1) {
            return this.windowLength;
        }
        return (int) Math.pow (2, Math.ceil (Math.log (this.fsLimit) / Math.log (2)));
    }

    @Override
    public Sound initSound (final Sound input) {
        if (this.note) {
            this.step = input.getSamplesLength ();
            this.fsLimit = input.getSamplesLength ();
            this.loudestfreqs = new float [1];
        } else {
            this.loudestfreqs = new float [(int) (input.getSamplesLength () / this.step) + 1];
            this.fsLimit = input.getSampleRate ();
        }
        this.allLoudestFreqs.add (this.loudestfreqs);
        this.soundLength = input.getSamplesLength ();
        return super.initSound (input);
    }

    @Override
    public Spectrum<T> transformFrequencies (final Spectrum<T> fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

        final int percent = (int) Math.floor (100.0 * (offset / this.step) / (this.soundLength / this.step));
        if (percent > Math.floor (100.0 * ((offset - this.step) / this.step) / (this.soundLength / this.step))) {
            this.log (new LogEvent (PeakFindSoundTransformationEventCode.ITERATION_IN_PROGRESS, (int) (offset / this.step), (int) Math.ceil (this.soundLength / this.step), percent));
        }
        float f0 = 0;

        if (soundLevelInDB > 30 || this.note) {
            final float [] peaks = new float [10];
            for (int i = 1 ; i <= 10 ; i++) {
                peaks [i - 1] = this.spectrumHelper.f0 (fs, i);
            }
            Arrays.sort (peaks);
            f0 = this.bestCandidate (peaks);
        }

        if (this.note) {
            this.detectedNoteVolume = soundLevelInDB;
        }
        this.loudestfreqs [(int) (offset / this.step)] = f0;
        return fs;
    }
}
