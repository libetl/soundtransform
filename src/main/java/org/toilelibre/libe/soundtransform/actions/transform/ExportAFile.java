package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class ExportAFile extends Action {

    public ExportAFile (final Observer... observers) {
        super (observers);
    }

    public void writeFile (final InputStream is, final File fDest) throws SoundTransformException {
        this.audioFile.fileFromStream (is, fDest);
    }
}
