package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.note.Note;

public interface SoundAppender {

    void append (Channel origin, int usedarraylength, Channel... otherSounds);

    int append (Channel origin, int usedarraylength, Channel otherSound);

    Channel append (Channel sound, Channel sound2);

    void appendNote (Channel sound, Note note, double lastFreq, int indexInSound, int channelNum, float lengthInSeconds) throws SoundTransformException;

    Channel changeNbBytesPerSample (Channel sound, int newNbBytesPerSample);

    Channel downsampleWithRatio (Channel sound, float ratio);

    Channel resizeToSampleRate (Channel sound, float newSampleRate);

}