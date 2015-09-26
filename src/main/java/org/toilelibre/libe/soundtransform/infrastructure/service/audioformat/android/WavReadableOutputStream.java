package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayOutputStream;

class WavReadableOutputStream extends WavOutputStream {

    public WavReadableOutputStream (final ByteArrayOutputStream stream) {
        super (stream);
    }

    public byte [] getContent () {
        return ((ByteArrayOutputStream) this.out).toByteArray ();
    }
}
