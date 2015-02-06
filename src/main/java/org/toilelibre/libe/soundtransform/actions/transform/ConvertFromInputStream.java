package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class ConvertFromInputStream extends Action {

    public ConvertFromInputStream (Observer... observers) {
        super (observers);
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.transformSound.fromInputStream (ais);
    }

    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        return this.transformSound.fromInputStream (ais, isInfo);
    }
}
