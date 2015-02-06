package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class GetInputStreamInfo extends Action {

    public GetInputStreamInfo (Observer... observers) {
        super (observers);
    }

    public InputStreamInfo getInputStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.transformSound.getInputStreamInfo (ais);
    }

    public InputStreamInfo getInputStreamInfo (final Sound [] sounds) throws SoundTransformException {
        return this.transformSound.getInputStreamInfo (sounds);
    }
}
