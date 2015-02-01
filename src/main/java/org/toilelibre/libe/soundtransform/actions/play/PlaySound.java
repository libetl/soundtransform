package org.toilelibre.libe.soundtransform.actions.play;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class PlaySound extends Action {

    public void play (final InputStream is) throws SoundTransformException {
        this.playSound.play (is);
    }

    public void play (final Sound [] channels) throws SoundTransformException {
        this.playSound.play (channels);
    }

    @SuppressWarnings ("unchecked")
    public void play (final Spectrum<?> spectrum1) throws SoundTransformException {
        final Spectrum<Object> spectrum = (Spectrum<Object>) spectrum1;
        ((PlaySoundService<Object>) this.playSound).play (spectrum);
    }
}
