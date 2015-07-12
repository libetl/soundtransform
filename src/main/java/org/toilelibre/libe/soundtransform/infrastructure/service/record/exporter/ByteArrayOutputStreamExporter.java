package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

import java.io.ByteArrayOutputStream;

import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteArrayOutputStream;

class ByteArrayOutputStreamExporter implements BytesExporterFromThread<ByteArrayOutputStream>, OutputAsByteArrayOutputStream {

    private ByteArrayOutputStream outputStream;

    public ByteArrayOutputStreamExporter () {
    }

    public void init (int bufferSize) {
        this.outputStream = new ByteArrayOutputStream (bufferSize);
    }
    
    @Override
    public void export (byte [] byteArray, int readSize) {
        this.outputStream.write (byteArray, 0, readSize);
    }

    public ByteArrayOutputStream getOutput () {
        return this.outputStream;
    }

}
