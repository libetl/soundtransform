package org.toilelibre.libe.soundtransform.model.record.exporter;

import java.io.ByteArrayOutputStream;

public interface OutputAsByteArrayOutputStream extends BytesExporterFromThread<ByteArrayOutputStream> {
    ByteArrayOutputStream getOutput ();
}
