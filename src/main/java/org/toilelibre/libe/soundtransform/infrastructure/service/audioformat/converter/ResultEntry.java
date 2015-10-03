package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

class ResultEntry implements Entry<StreamInfo, ByteArrayOutputStream> {

    private final StreamInfo            streamInfo;
    private final ByteArrayOutputStream outputStream;

    public ResultEntry (final StreamInfo streamInfo1, final ByteArrayOutputStream outputStream1) {
        this.streamInfo = streamInfo1;
        this.outputStream = outputStream1;
    }

    @Override
    public StreamInfo getKey () {
        return this.streamInfo;
    }

    @Override
    public ByteArrayOutputStream getValue () {
        return this.outputStream;
    }

    @Override
    public ByteArrayOutputStream setValue (final ByteArrayOutputStream object) {
        throw new UnsupportedOperationException ();
    }

}