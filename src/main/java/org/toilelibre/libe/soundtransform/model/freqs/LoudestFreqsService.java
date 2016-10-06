package org.toilelibre.libe.soundtransform.model.freqs;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Service
public interface LoudestFreqsService {

    List<float []> adjust (List<float []> freqs);

    List<float []> compress (List<float []> freqs, float factor);

    List<float []> filterRange (List<float []> freqs, float low, float high) throws SoundTransformException;

    List<float []> insertPart (List<float []> freqs, List<float []> subFreqs, int start);

    List<float []> octaveDown (List<float []> freqs);

    List<float []> octaveUp (List<float []> freqs);

    List<float []> replacePart (List<float []> freqs, List<float []> subFreqs, int start);

    List<float []> surroundInRange (List<float []> freqs, float low, float high) throws SoundTransformException;

}