package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.library.note.Note;

public interface SoundAppender {

    public abstract void append (Sound origin, int usedarraylength, Sound... otherSounds);

    public abstract int append (Sound origin, int usedarraylength, Sound otherSound);

    public abstract void appendNote (Sound sound, Note note, double lastFreq, int indexInSound, int channelNum, float lengthInSeconds);

    public abstract Sound changeNbBytesPerSample (Sound sound, int newNbBytesPerSample);

    public abstract Sound downsampleWithRatio (Sound sound, float ratio);

    public abstract Sound resizeToSampleRate (Sound sound, int newfreq);

}