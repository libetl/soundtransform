package org.toilelibre.libe.soundtransform.model.freqs;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface LoudestFreqsService {

    public abstract List<float []> adjust (List<float []> freqs);

    public abstract List<float []> compress (List<float []> freqs, float factor);

    public abstract List<float []> filterRange (List<float []> freqs, float low, float high) throws SoundTransformException;

    public abstract List<float []> insertPart (List<float []> freqs, List<float []> subFreqs, int start);

    public abstract List<float []> octaveDown (List<float []> freqs);

    public abstract List<float []> octaveUp (List<float []> freqs);

    public abstract List<float []> replacePart (List<float []> freqs, List<float []> subFreqs, int start);

    public abstract List<float []> surroundInRange (List<float []> freqs, float low, float high) throws SoundTransformException;

}