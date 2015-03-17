package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class ToInputStream extends Action {

    public ToInputStream(final Observer... observers) {
        super(observers);
    }

    public InputStream toStream(final File fOrigin) throws SoundTransformException {
        return this.transformSound.fromFile(fOrigin);
    }

    public InputStream toStream(final Sound[] channels, final StreamInfo streamInfo) throws SoundTransformException {
        return this.transformSound.toStream(channels, streamInfo);
    }
}
