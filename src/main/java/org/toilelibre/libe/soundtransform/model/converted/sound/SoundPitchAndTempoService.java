package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Service
public interface SoundPitchAndTempoService {

    Channel callTransform (Channel sound, float percent, float lengthInSeconds) throws SoundTransformException;

}