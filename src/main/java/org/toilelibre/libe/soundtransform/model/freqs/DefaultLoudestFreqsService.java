package org.toilelibre.libe.soundtransform.model.freqs;

import java.util.Arrays;
import java.util.List;

final class DefaultLoudestFreqsService implements LoudestFreqsService {

    private static final float                 HALF  = 0.5f;
    private static final float                 TWICE = 2.0f;

    private final ChangeOctaveProcessor        changeOctaveProcessor;
    private final AdjustFrequenciesProcessor   adjustFrequenciesProcessor;
    private final FilterFrequenciesProcessor   filterFrequenciesProcessor;
    private final ReplaceFrequenciesProcessor  replaceFrequenciesProcessor;
    private final CompressFrequenciesProcessor compressFrequenciesProcessor;

    public DefaultLoudestFreqsService (final ChangeOctaveProcessor changeOctaveProcessor1, final AdjustFrequenciesProcessor adjustFrequenciesProcessor1, final FilterFrequenciesProcessor filterFrequenciesProcessor1, final ReplaceFrequenciesProcessor replaceFrequenciesProcessor1,
            final CompressFrequenciesProcessor compressFrequenciesProcessor1) {
        this.changeOctaveProcessor = changeOctaveProcessor1;
        this.adjustFrequenciesProcessor = adjustFrequenciesProcessor1;
        this.filterFrequenciesProcessor = filterFrequenciesProcessor1;
        this.replaceFrequenciesProcessor = replaceFrequenciesProcessor1;
        this.compressFrequenciesProcessor = compressFrequenciesProcessor1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#adjust
     * (float[])
     */
    @Override
    public List<float []> adjust (final List<float []> freqs) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.adjustFrequenciesProcessor.adjust (freqs.get (i));
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#compress
     * (float[], float)
     */
    @Override
    public List<float []> compress (final List<float []> freqs, final float factor) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.compressFrequenciesProcessor.compress (freqs.get (i), factor);
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#
     * filterRange(float[], float, float)
     */
    @Override
    public List<float []> filterRange (final List<float []> freqs, final float low, final float high) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.filterFrequenciesProcessor.filter (freqs.get (i), low, high);
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#insertPart
     * (float[], float[], int)
     */
    @Override
    public List<float []> insertPart (final List<float []> freqs, final List<float []> subFreqs, final int start) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.replaceFrequenciesProcessor.insertPart (freqs.get (i), subFreqs.get (i), start);
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#octaveDown
     * (float[])
     */
    @Override
    public List<float []> octaveDown (final List<float []> freqs) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.changeOctaveProcessor.multFreqs (freqs.get (i), DefaultLoudestFreqsService.HALF);
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#octaveUp
     * (float[])
     */
    @Override
    public List<float []> octaveUp (final List<float []> freqs) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.changeOctaveProcessor.multFreqs (freqs.get (i), DefaultLoudestFreqsService.TWICE);
        }
        return Arrays.asList (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService#
     * replacePart(float[], float[], int)
     */
    @Override
    public List<float []> replacePart (final List<float []> freqs, final List<float []> subFreqs, final int start) {
        final float [][] result = new float [freqs.size ()] [];
        for (int i = 0 ; i < freqs.size () ; i++) {
            result [i] = this.replaceFrequenciesProcessor.replacePart (freqs.get (i), subFreqs.get (i), start);
        }
        return Arrays.asList (result);
    }
}
