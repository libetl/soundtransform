package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;

public final class ExportAFileFromAFileUsingSoundTransform extends Action {

    public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws UnsupportedAudioFileException, IOException {
        this.transformSound.transformFile (fOrigin, fDest, sts);
    }

}
