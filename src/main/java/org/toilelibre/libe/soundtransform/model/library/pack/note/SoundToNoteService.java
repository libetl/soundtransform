package org.toilelibre.libe.soundtransform.model.library.pack.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface SoundToNoteService {

    Note convert (SimpleNoteInfo noteInfo, Sound sound) throws SoundTransformException;

}