package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;

public interface SoundToNoteService {

    public abstract Note convert (SimpleNoteInfo noteInfo, Sound [] channels) throws SoundTransformException;

}