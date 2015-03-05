package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class GetSoundInfo extends Action {

    public GetSoundInfo (final Observer... observers) {
        super (observers);
    }

    public StreamInfo getSoundInfo (final InputStream ais) throws SoundTransformException {
        return this.transformSound.getSoundInfo (ais);
    }

    public FormatInfo getSoundInfo (final Sound [] sounds) throws SoundTransformException {
        return this.transformSound.getSoundInfo (sounds);
    }
}
