package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Action
public final class AppendSound {

    private final ModifySoundService modifySound;

    public AppendSound () {
        this.modifySound = ApplicationInjector.$.select (ModifySoundService.class);
    }

    public Sound append (final Sound sound1, final Sound sound2) throws SoundTransformException {
        return this.modifySound.append (sound1, sound2);
    }
}
