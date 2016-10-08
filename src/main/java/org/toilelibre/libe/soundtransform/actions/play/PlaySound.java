package org.toilelibre.libe.soundtransform.actions.play;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectService;

@Action
public class PlaySound {

    private final PlayObjectService<?> playSound;

    public PlaySound () {
        this.playSound = ApplicationInjector.$.select (PlayObjectService.class);
    }

    public void play (final InputStream is, final Object stopMonitor, final int skipMilliSeconds) throws SoundTransformException {
        this.playSound.play (is, stopMonitor, skipMilliSeconds);
    }

    public void play (final Sound sound, final Object stopMonitor, final int skipMilliSeconds) throws SoundTransformException {
        this.playSound.play (sound, stopMonitor, skipMilliSeconds);
    }

    @SuppressWarnings ("unchecked")
    public void play (final Spectrum<? extends Serializable> spectrum1, final Object stopMonitor, int skipMilliSeconds) throws SoundTransformException {
        final Spectrum<Serializable> spectrum = (Spectrum<Serializable>) spectrum1;
        ((PlayObjectService<Serializable>) this.playSound).play (spectrum, stopMonitor, skipMilliSeconds);
    }
}
