package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public interface PlayObjectProcessor {

    Object play (InputStream ais, StreamInfo streamInfo) throws PlayObjectException;
}
