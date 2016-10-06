package org.toilelibre.libe.soundtransform.actions.play;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectService;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundService;

@Action
public class PlaySound {

    private final PlayObjectService playSound;

    public PlaySound () {
        this.playSound = ApplicationInjector.$.select (PlayObjectService.class);
    }

    public void play (final InputStream is, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException {
        this.playSound.play (is, stopMonitor, skipMilliSeconds);
    }

    public void play (final Sound sound, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException {
        this.playSound.play (sound, stopMonitor, skipMilliSeconds);
    }

    @SuppressWarnings ("unchecked")
    public void play (final Spectrum<? extends Serializable> spectrum1, Object stopMonitor, int skipMilliSeconds) throws SoundTransformException {
        final Spectrum<Serializable> spectrum = (Spectrum<Serializable>) spectrum1;
        ((PlayObjectService<Serializable>) this.playSound).play (spectrum, stopMonitor, skipMilliSeconds);
    }
}
