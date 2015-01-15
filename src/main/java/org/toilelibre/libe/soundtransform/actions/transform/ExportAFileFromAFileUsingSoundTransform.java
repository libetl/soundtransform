package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class ExportAFileFromAFileUsingSoundTransform extends Action {

    public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws SoundTransformException {
        this.transformSound.transformFile (fOrigin, fDest, sts);
    }

}
