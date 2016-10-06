package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

@Action
public final class ConvertFromInputStream {

    private final InputStreamToSoundService<?> is2Sound;

    public ConvertFromInputStream (final Observer... observers) {
        this.is2Sound = (InputStreamToSoundService<?>) ApplicationInjector.$.select (InputStreamToSoundService.class).setObservers (observers);
    }

    public Sound fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.is2Sound.fromInputStream (ais);
    }

    public Sound fromInputStream (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        return this.is2Sound.fromInputStream (ais, isInfo);
    }
}
