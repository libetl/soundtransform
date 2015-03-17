package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;

public interface PlaySoundProcessor {

    Object play(InputStream ais) throws PlaySoundException;
}
