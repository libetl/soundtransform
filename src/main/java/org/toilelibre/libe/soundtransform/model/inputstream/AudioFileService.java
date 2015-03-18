package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class AudioFileService extends AbstractLogAware<AudioFileService> {

    private final AudioFileHelper   audioFileHelper;
    private final AudioFormatParser audioFormatParser;

    public AudioFileService (final AudioFileHelper helper1, final AudioFormatParser audioFormatParser1) {
        this (helper1, audioFormatParser1, new Observer [0]);
    }

    public AudioFileService (final AudioFileHelper helper1, final AudioFormatParser audioFormatParser1, final Observer... observers1) {
        this.audioFileHelper = helper1;
        this.audioFormatParser = audioFormatParser1;
        this.observers = observers1;
    }

    public InputStream streamFromFile (final File file) throws SoundTransformException {
        return this.audioFileHelper.getAudioInputStream (file);
    }

    public InputStream streamFromRawStream (final InputStream is, final StreamInfo streamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (is, this.audioFormatParser.audioFormatfromStreamInfo (streamInfo));
    }

    public void fileFromStream (final InputStream ais2, final File fDest) throws SoundTransformException {
        this.audioFileHelper.writeInputStream (ais2, fDest);
    }
}
