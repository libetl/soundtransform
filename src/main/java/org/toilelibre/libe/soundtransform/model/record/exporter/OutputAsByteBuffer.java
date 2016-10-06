package org.toilelibre.libe.soundtransform.model.record.exporter;

import java.nio.ByteBuffer;

public interface OutputAsByteBuffer extends BytesExporterFromThread<ByteBuffer> {
    ByteBuffer getOutput ();
}
