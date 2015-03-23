package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

final class ByteArrayWithAudioFormatInputStream extends ByteArrayInputStream implements HasStreamInfo {

    private final StreamInfo info;

    public ByteArrayWithAudioFormatInputStream (final byte [] buf, final StreamInfo info1) {
        super (buf);
        this.info = info1;
    }

    public byte [] getAllContent () {
        return this.buf.clone ();
    }

    @Override
    public StreamInfo getInfo () {
        return this.info;
    }

}
