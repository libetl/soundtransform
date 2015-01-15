package org.toilelibre.libe.soundtransform.actions.play;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class PlaySound extends Action {

    public void play (final Sound [] channels) throws SoundTransformException {
        final PlaySoundService<?> ps = $.create (PlaySoundService.class);
        ps.play (channels);
    }
}
