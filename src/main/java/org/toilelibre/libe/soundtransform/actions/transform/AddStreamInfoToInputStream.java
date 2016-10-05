package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

public final class AddStreamInfoToInputStream extends Action {

    public AddStreamInfoToInputStream (final Observer... observers) {
        super (observers);
    }

    public InputStream transformRawInputStream (final InputStream ais, final StreamInfo isi) throws SoundTransformException {
        return this.audioFile.streamFromRawStream (ais, isi);
    }

}
