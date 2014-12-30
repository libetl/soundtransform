package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ConvertAudioFileService {

    private final AudioFileHelper    audioFileHelper;

    public ConvertAudioFileService () {
        this.audioFileHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.JavazoomAudioFileHelper ();
    }

    public AudioInputStream callConverter (final File file) throws UnsupportedAudioFileException, IOException {
        return this.audioFileHelper.getAudioInputStream (file);
    }
}
