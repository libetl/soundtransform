package org.toilelibre.libe.soundtransform.model.inputstream.format;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

@Service
final class DefaultAudioFormatService implements AudioFormatService {

    private final AudioFormatParser parser;

    public DefaultAudioFormatService (final AudioFormatParser parser1) {
        this.parser = parser1;
    }

    @Override
    public Object audioFormatfromStreamInfo (final StreamInfo info) {
        return this.parser.audioFormatfromStreamInfo (info);
    }

    @Override
    public StreamInfo fromAudioFormat (final Object audioFormat1, final long l) throws SoundTransformException {
        return this.parser.fromAudioFormat (audioFormat1, l);
    }

    @Override
    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.parser.getStreamInfo (ais);
    }

}
