package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public interface PlaySoundProcessor {

    Object play (InputStream ais, StreamInfo streamInfo) throws PlaySoundException;
}
