package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectService;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundService;

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
