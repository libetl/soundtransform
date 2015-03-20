package org.toilelibre.libe.soundtransform.actions.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class RecordSound extends Action {

    public InputStream record (Object stop) throws SoundTransformException {
        return this.recordSound.record (stop);
    }

}
