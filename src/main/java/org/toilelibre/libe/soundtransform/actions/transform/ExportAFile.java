package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

@Action
public final class ExportAFile {

    private final AudioFileService<?> audioFile;

    public ExportAFile (final Observer... observers) {
        this.audioFile = (AudioFileService<?>) ApplicationInjector.$.select (AudioFileService.class).setObservers (observers);
    }

    public void writeFile (final InputStream is, final File fDest) throws SoundTransformException {
        this.audioFile.fileFromStream (is, fDest);
    }
}
