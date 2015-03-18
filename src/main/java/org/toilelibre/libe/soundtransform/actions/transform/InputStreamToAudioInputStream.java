package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class InputStreamToAudioInputStream extends Action {

    public InputStreamToAudioInputStream (final Observer... observers) {
        super (observers);
    }

    public InputStream transformRawInputStream (final InputStream ais, final StreamInfo isi) throws SoundTransformException {
        return this.audioFile.streamFromRawStream (ais, isi);
    }

}
