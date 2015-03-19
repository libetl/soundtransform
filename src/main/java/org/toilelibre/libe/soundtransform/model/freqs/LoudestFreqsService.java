package org.toilelibre.libe.soundtransform.model.freqs;

public interface LoudestFreqsService {

    public abstract float[] adjust(float[] freqs);

    public abstract float[] compress(float[] freqs, float factor);

    public abstract float[] filterRange(float[] freqs, float low, float high);

    public abstract float[] insertPart(float[] freqs, float[] subFreqs, int start);

    public abstract float[] octaveDown(float[] freqs);

    public abstract float[] octaveUp(float[] freqs);

    public abstract float[] replacePart(float[] freqs, float[] subFreqs, int start);

}