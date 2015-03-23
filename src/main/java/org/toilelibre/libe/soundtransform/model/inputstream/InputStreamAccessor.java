package org.toilelibre.libe.soundtransform.model.inputstream;

import org.toilelibre.libe.soundtransform.model.freqs.FreqsAccessor;

public abstract class InputStreamAccessor extends FreqsAccessor {

    public InputStreamAccessor () {
        super ();
        this.usedImpls.put (AudioFileService.class, DefaultAudioFileService.class);
        this.usedImpls.put (InputStreamToSoundService.class, DefaultInputStreamToSoundService.class);
        this.usedImpls.put (SoundToInputStreamService.class, DefaultSoundToInputStreamService.class);
    }
}
