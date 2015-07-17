package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.nio.ByteBuffer;

import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteBuffer;

class ByteBufferExporter implements BytesExporterFromThread<ByteBuffer>, OutputAsByteBuffer {

    private ByteBuffer buffer;

    public ByteBufferExporter () {
    }

    @Override
    public void init (final int bufferSize) {
        this.buffer = ByteBuffer.allocate (bufferSize);
    }

    @Override
    public void export (final byte [] byteArray, final int readSize) {
        this.buffer.rewind ();
        this.buffer.put (byteArray, 0, readSize);
        synchronized (this.buffer) {
            this.buffer.notify ();
        }
    }

    @Override
    public ByteBuffer getOutput () {
        return this.buffer;
    }

}
