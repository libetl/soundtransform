package org.toilelibre.libe.soundtransform.model.inputstream.format;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

@Service
public interface AudioFormatService {
    Object audioFormatfromStreamInfo (StreamInfo info);

    StreamInfo fromAudioFormat (Object audioFormat1, long l) throws SoundTransformException;

    StreamInfo getStreamInfo (InputStream ais) throws SoundTransformException;
}
