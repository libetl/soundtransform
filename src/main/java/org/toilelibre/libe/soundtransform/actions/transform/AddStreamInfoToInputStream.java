package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

@Action
public final class AddStreamInfoToInputStream {

    private AudioFileService<?> audioFile;

    public AddStreamInfoToInputStream (final Observer... observers) {
        this.audioFile = (AudioFileService<?>) ApplicationInjector.$.select (AudioFileService.class).setObservers (observers);
    }

    public InputStream transformRawInputStream (final InputStream ais, final StreamInfo isi) throws SoundTransformException {
        return this.audioFile.streamFromRawStream (ais, isi);
    }

}
