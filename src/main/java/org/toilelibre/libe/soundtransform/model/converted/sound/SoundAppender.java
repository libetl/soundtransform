package org.toilelibre.libe.soundtransform.model.converted.sound;

public interface SoundAppender {

	public abstract void append (Sound origin, int usedarraylength, Sound... otherSounds);

	public abstract int append (Sound origin, int usedarraylength, Sound otherSound);

	public abstract Sound changeNbBytesPerSample (Sound sound, int newNbBytesPerSample);

	public abstract Sound resizeToSampleRate (Sound sound, int newfreq);

	public abstract Sound downsampleWithRatio (Sound sound, float ratio);

}