package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

@Action
public final class ConvertToInputStream {

    private final AudioFileService<?> audioFile;
    private final SoundToInputStreamService<?> sound2is;

    public ConvertToInputStream (final Observer... observers) {
        this.audioFile = (AudioFileService<?>) ApplicationInjector.$.select (AudioFileService.class).setObservers (observers);
        this.sound2is = (SoundToInputStreamService<?>) ApplicationInjector.$.select (SoundToInputStreamService.class).setObservers (observers);
    }

    public InputStream toStream (final File fOrigin) throws SoundTransformException {
        return this.audioFile.streamFromFile (fOrigin);
    }

    public InputStream toStream (final Sound sound, final StreamInfo streamInfo) throws SoundTransformException {
        return this.sound2is.toStream (sound, streamInfo);
    }
}
