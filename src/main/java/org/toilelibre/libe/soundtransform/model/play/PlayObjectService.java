package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Service
public interface PlayObjectService<T extends Serializable> {

    Object play (InputStream is, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException;

    Object play (Sound sound, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException;

    Object play (Spectrum<T> spectrum, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException;

}