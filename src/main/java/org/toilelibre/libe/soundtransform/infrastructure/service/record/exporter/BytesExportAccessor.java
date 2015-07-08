package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import org.toilelibre.libe.soundtransform.infrastructure.service.pack.PackAccessor;

public abstract class BytesExportAccessor extends PackAccessor {

    protected OutputAsByteArrayOutputStream providerByteArrayOutputStreamExporter () {
        return new ByteArrayOutputStreamExporter ();
    }

    protected OutputAsByteBuffer providerByteBufferOutputStreamExporter () {
        return new ByteBufferExporter ();
    }
}
