package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

public class GetStreamInfo extends Action {

    public GetStreamInfo (final Observer... observers) {
        super (observers);
    }

    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.is2Sound.getStreamInfo (ais);
    }
}
