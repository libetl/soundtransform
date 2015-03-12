package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class GetStreamInfo extends Action {

    public GetStreamInfo (final Observer... observers) {
        super (observers);
    }

    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.transformSound.getStreamInfo (ais);
    }

    public FormatInfo getFormatInfo (final Sound [] sounds) throws SoundTransformException {
        return this.transformSound.getFormatInfo (sounds);
    }
}
