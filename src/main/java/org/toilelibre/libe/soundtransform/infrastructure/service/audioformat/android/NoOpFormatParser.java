package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class NoOpFormatParser implements AudioFormatParser {

    @Override
    public Object audioFormatfromInputStreamInfo (final InputStreamInfo info) {
        return info;
    }

    @Override
    public InputStreamInfo fromAudioFormat (final Object audioFormat1, final long l) {
        return (InputStreamInfo) audioFormat1;
    }

    @Override
    public InputStreamInfo getInputStreamInfo (final InputStream is) throws SoundTransformException {
        if (is instanceof HasInputStreamInfo) {
            return ((HasInputStreamInfo) is).getInfo ();
        }
        try {
            return new AndroidWavHelper ().readMetadata (new AudioInputStream (is));
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFormatParserErrorCode.READ_ERROR, e);
        }
    }
}
