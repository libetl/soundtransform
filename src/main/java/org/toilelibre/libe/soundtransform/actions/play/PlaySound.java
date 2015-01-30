package org.toilelibre.libe.soundtransform.actions.play;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class PlaySound extends Action {

    public void play (final Sound [] channels) throws SoundTransformException {
        this.playSound.play (channels);
    }

    public void play (final InputStream is) throws SoundTransformException {
        this.playSound.play (is);
    }
}
