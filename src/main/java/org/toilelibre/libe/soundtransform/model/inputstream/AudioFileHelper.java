package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public interface AudioFileHelper {

    public AudioInputStream getAudioInputStream (File inputFile) throws UnsupportedAudioFileException, IOException;
}
