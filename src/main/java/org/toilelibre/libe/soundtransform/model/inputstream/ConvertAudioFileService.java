package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class ConvertAudioFileService {

    private final AudioFileHelper   audioFileHelper;
    private final AudioFormatParser audioFormatParser;

    public ConvertAudioFileService (final AudioFileHelper helper1, final AudioFormatParser parser1) {
        this.audioFileHelper = helper1;
        this.audioFormatParser = parser1;
    }

    public InputStreamInfo callAudioFormatParser (final InputStream is) throws SoundTransformException {
        return this.audioFormatParser.getInputStreamInfo (is);
    }

    public InputStream callConverter (final File file) throws SoundTransformException {
        return this.audioFileHelper.getAudioInputStream (file);
    }

    public InputStream toStream (final byte [] byteArray, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (byteArray, this.audioFormatParser.audioFormatfromInputStreamInfo (inputStreamInfo));
    }

    public InputStream toStream (final InputStream is, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (is, this.audioFormatParser.audioFormatfromInputStreamInfo (inputStreamInfo));
    }

    public void writeInputStream (final InputStream ais2, final File fDest) throws SoundTransformException {
        this.audioFileHelper.writeInputStream (ais2, fDest);
    }
}
