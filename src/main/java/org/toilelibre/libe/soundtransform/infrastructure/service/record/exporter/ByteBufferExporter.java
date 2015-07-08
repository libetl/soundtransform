package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.nio.ByteBuffer;

class ByteBufferExporter implements BytesExporterFromThread<ByteBuffer>, OutputAsByteBuffer {

    private ByteBuffer buffer;

    public ByteBufferExporter () {
    }
    
    public void init (int bufferSize) {
        this.buffer = ByteBuffer.allocate (bufferSize);
    }

    @Override
    public void export (byte [] byteArray, int readSize) {
        this.buffer.rewind ();
        this.buffer.put (byteArray, 0, readSize);
        synchronized (this.buffer) {
            this.buffer.notify ();
        }
    }

    public ByteBuffer getOutput () {
        return this.buffer;
    }

}
