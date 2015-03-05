package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class NoOpFormatParser implements AudioFormatParser {

    @Override
    public Object audioFormatfromSoundInfo (final StreamInfo info) {
        return info;
    }

    @Override
    public StreamInfo fromAudioFormat (final Object audioFormat1, final long l) {
        return (StreamInfo) audioFormat1;
    }

    @Override
    public StreamInfo getSoundInfo (final InputStream is) throws SoundTransformException {
        if (is instanceof HasSoundInfo) {
            return ((HasSoundInfo) is).getInfo ();
        }
        try {
            return new AndroidWavHelper ().readMetadata (new AudioInputStream (is));
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFormatParserErrorCode.READ_ERROR, e);
        }
    }
}
