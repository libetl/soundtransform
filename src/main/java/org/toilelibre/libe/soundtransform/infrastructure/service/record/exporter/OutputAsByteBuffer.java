package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.nio.ByteBuffer;

public interface OutputAsByteBuffer extends BytesExporterFromThread<ByteBuffer> {
    ByteBuffer getOutput ();
}
