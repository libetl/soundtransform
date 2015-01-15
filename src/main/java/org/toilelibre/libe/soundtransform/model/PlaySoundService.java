package org.toilelibre.libe.soundtransform.model;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface PlaySoundService {

    Object play (InputStream ais) throws PlaySoundException;

    Object play (Sound [] channels) throws SoundTransformException;

    Object play (Spectrum spectrum) throws SoundTransformException;
}
