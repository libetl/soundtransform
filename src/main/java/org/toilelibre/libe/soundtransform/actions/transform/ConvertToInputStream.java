package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

public final class ConvertToInputStream extends Action {

    public ConvertToInputStream (final Observer... observers) {
        super (observers);
    }

    public InputStream toStream (final File fOrigin) throws SoundTransformException {
        return this.audioFile.streamFromFile (fOrigin);
    }

    public InputStream toStream (final Sound sound, final StreamInfo streamInfo) throws SoundTransformException {
        return this.sound2is.toStream (sound, streamInfo);
    }
}