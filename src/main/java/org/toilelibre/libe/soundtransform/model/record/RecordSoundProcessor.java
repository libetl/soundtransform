package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface RecordSoundProcessor {

    InputStream recordRawInputStream (Object audioFormat, Object stop) throws SoundTransformException;

    ByteBuffer startRecordingAndReturnByteBuffer (Object audioFormat, Object stop) throws SoundTransformException;
}
