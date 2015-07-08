package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.io.ByteArrayOutputStream;

public interface OutputAsByteArrayOutputStream extends BytesExporterFromThread<ByteArrayOutputStream> {
    ByteArrayOutputStream getOutput ();
}
