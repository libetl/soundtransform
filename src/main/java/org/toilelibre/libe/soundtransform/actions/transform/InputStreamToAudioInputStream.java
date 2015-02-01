package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public final class InputStreamToAudioInputStream extends Action {

    public InputStream transformRawInputStream (final InputStream ais, final InputStreamInfo isi) throws SoundTransformException {
        return this.transformSound.transformRawInputStream (ais, isi);
    }

}
