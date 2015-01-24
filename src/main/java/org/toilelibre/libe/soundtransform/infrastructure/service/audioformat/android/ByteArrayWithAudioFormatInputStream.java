package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class ByteArrayWithAudioFormatInputStream extends ByteArrayInputStream implements HasInputStreamInfo {

    private final InputStreamInfo info;

    public ByteArrayWithAudioFormatInputStream (byte [] buf, InputStreamInfo info1) {
        super (buf);
        this.info = info1;
    }

    public InputStreamInfo getInfo () {
        return this.info;
    }

}
