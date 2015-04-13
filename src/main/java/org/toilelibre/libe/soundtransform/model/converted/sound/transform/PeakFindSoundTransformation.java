package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface PeakFindSoundTransformation<T extends Serializable> extends SoundTransformation, LogAware<AbstractLogAware<AbstractFrequencySoundTransformation<T>>> {

    float [] getLoudestFreqs ();

    float getDetectedNoteVolume ();
}
