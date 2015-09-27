package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface PlaySoundService<T extends Serializable> {

    Object play (InputStream is) throws SoundTransformException;

    Object play (Sound sound) throws SoundTransformException;

    Object play (Spectrum<T> spectrum) throws SoundTransformException;

}