package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface AudioFormatParser {
    public abstract Object audioFormatfromInputStreamInfo (InputStreamInfo info);

    public abstract InputStreamInfo fromAudioFormat (Object audioFormat1, long l);

    public abstract InputStreamInfo getInputStreamInfo (InputStream ais)
            throws SoundTransformException;
}
