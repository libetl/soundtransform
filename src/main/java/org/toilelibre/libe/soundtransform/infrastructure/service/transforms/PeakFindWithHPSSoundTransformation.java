package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class PeakFindWithHPSSoundTransformation extends SimpleFrequencySoundTransformation {

    private double               threshold;
    private List<Integer>        loudestfreqs;
    private boolean              note;
    private int                  fsLimit;
    private int                  windowLength;
    private int                  soundLength;
    private final SpectrumHelper spectrumHelper;

    private PeakFindWithHPSSoundTransformation () {
        this.spectrumHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.HPSSpectrumHelper ();
    }

    public PeakFindWithHPSSoundTransformation (final boolean note) {
        this ();
        this.note = note;
        this.threshold = 100;
        this.windowLength = -1;
        this.soundLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (final double threshold) {
        this ();
        this.threshold = threshold;
        this.windowLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (final double threshold, final int windowLength) {
        this ();
        this.threshold = threshold;
        this.windowLength = windowLength;
    }

    private int bestCandidate (final int [] peaks) {
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

        return rightEdge == leftEdge ? sum : sum / (rightEdge - leftEdge);
    }

    public List<Integer> getLoudestFreqs () {
        return this.loudestfreqs;
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
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
        this.loudestfreqs = new ArrayList<Integer> ();
        if (this.note) {
            this.threshold = input.getSamples ().length;
            this.fsLimit = input.getSamples ().length;
        } else {
            this.fsLimit = input.getSampleRate ();
        }
        this.soundLength = input.getSamples ().length;
        return super.initSound (input);
    }

    @Override
    public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

        final int percent = (int) Math.floor (100.0 * (offset / this.threshold) / (this.soundLength / this.threshold));
        if (percent > Math.floor (100.0 * ((offset - this.threshold) / this.threshold) / (this.soundLength / this.threshold))) {
            this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + (int) (offset / this.threshold) + " / "
                    + (int) Math.ceil (this.soundLength / this.threshold) + ", " + percent + "%"));
        }
        int f0 = 0;

        if (soundLevelInDB > 30) {
            final int [] peaks = new int [10];
            for (int i = 1 ; i <= 10 ; i++) {
                peaks [i - 1] = this.spectrumHelper.f0 (fs, i);
            }
            Arrays.sort (peaks);
            f0 = this.bestCandidate (peaks);
        }

        this.loudestfreqs.add (f0);
        return fs;
    }
}
