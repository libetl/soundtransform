package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.note.Note;

public interface SoundAppender {

    public abstract void append (Channel origin, int usedarraylength, Channel... otherSounds);

    public abstract int append (Channel origin, int usedarraylength, Channel otherSound);

    public abstract Channel append (Channel sound, Channel sound2);

    public abstract void appendNote (Channel sound, Note note, double lastFreq, int indexInSound, int channelNum, float lengthInSeconds) throws SoundTransformException;

    public abstract Channel changeNbBytesPerSample (Channel sound, int newNbBytesPerSample);

    public abstract Channel downsampleWithRatio (Channel sound, float ratio);

    public abstract Channel resizeToSampleRate (Channel sound, float newSampleRate);

}