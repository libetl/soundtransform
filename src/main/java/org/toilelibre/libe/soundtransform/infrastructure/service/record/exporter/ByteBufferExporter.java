package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.nio.ByteBuffer;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteBuffer;

@Processor
class ByteBufferExporter implements BytesExporterFromThread<ByteBuffer>, OutputAsByteBuffer {

    private ByteBuffer buffer;
    private int bufferSize;

    ByteBufferExporter () {
    }

    @Override
    public void init (final int bufferSize1) {
        this.bufferSize = bufferSize1;
        this.buffer = ByteBuffer.allocate (bufferSize1);
    }

    @Override
    public int getReportedBufferSize () {
        return this.bufferSize;
    }

    @Override
    public void export (final byte [] byteArray, final int readSize) {
        this.buffer.rewind ();
        this.buffer.put (byteArray, 0, readSize);
        synchronized (this.buffer) {
            this.buffer.notifyAll ();
        }
    }

    @Override
    public ByteBuffer getOutput () {
        return this.buffer;
    }

}
