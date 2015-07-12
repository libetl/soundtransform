package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import org.toilelibre.libe.soundtransform.infrastructure.service.pack.PackAccessor;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteArrayOutputStream;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteBuffer;

public abstract class BytesExportAccessor extends PackAccessor {

    protected OutputAsByteArrayOutputStream providerByteArrayOutputStreamExporter () {
        return new ByteArrayOutputStreamExporter ();
    }

    protected OutputAsByteBuffer providerByteBufferOutputStreamExporter () {
        return new ByteBufferExporter ();
    }
}
