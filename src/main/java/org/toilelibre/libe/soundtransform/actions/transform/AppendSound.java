package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class AppendSound extends Action {

    public AppendSound (final Observer... observers) {
        super (observers);
    }

    public Sound append (final Sound sound1, final Sound sound2) throws SoundTransformException {
        return new Sound (this.modifySound.append (sound1.getChannels(), sound2.getChannels()));
    }
}
