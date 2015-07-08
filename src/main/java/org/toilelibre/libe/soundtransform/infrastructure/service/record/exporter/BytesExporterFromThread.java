package org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter;

public interface BytesExporterFromThread<T> {

    void init (int bufferSize);
    
    void export (byte [] byteArray, int readSize);
    
    T getOutput ();
}
