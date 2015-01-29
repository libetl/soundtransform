package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class ExportAFile extends Action {

    public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws SoundTransformException {
        this.transformSound.transformFile (fOrigin, fDest, sts);
    }

    public void writeFile (InputStream is, File fDest) throws SoundTransformException {
        this.transformSound.writeFile (is, fDest);
    }
}
