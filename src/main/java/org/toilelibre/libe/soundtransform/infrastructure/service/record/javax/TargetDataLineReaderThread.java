package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import javax.sound.sampled.TargetDataLine;

import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;

final class TargetDataLineReaderThread extends Thread {
    /**
     *
     */
    private final TargetDataLine             dataLine;
    private boolean                          isRecording;
    private final BytesExporterFromThread<?> exporter;
    private static final int                 FIVE        = 5;

    /**
     * @param dataLine1
     */
    TargetDataLineReaderThread (final TargetDataLine dataLine1, final BytesExporterFromThread<?> exporter1) {
        this.dataLine = dataLine1;
        this.exporter = exporter1;
        this.setName (this.getClass ().getSimpleName ());
    }

    public void stopRecording () {
        this.isRecording = false;
    }

    @Override
    public void run () {
        this.isRecording = true;
        final byte [] data = new byte [this.dataLine.getBufferSize () / TargetDataLineReaderThread.FIVE];
        while (this.isRecording) {
            // Read the next chunk of data from the TargetDataLine.
            final int numBytesRead = this.dataLine.read (data, 0, data.length);
            // Save this chunk of data.
            this.exporter.export (data, numBytesRead);
        }
    }

}