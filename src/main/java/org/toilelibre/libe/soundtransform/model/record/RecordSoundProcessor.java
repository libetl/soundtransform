package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface RecordSoundProcessor {

    InputStream recordRawInputStream (Object audioFormat, Object stop) throws SoundTransformException;
}
