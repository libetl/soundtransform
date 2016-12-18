package org.toilelibre.libe.soundtransform.model.record.exporter;

public interface BytesExporterFromThread<T> {

    void init (int bufferSize);

    int getReportedBufferSize ();

    void export (byte [] byteArray, int readSize);

    T getOutput ();
}
