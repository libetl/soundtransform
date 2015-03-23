package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.TargetDataLine;

final class TargetDataLineReaderThread extends Thread {
    /**
     *
     */
    private final TargetDataLine        dataLine;
    private final ByteArrayOutputStream baos;
    private boolean               isRecording = false;
    private static final int      FIVE        = 5;

    /**
     * @param dataLine1
     */
    TargetDataLineReaderThread (final TargetDataLine dataLine1) {
        this.dataLine = dataLine1;
        this.baos = new ByteArrayOutputStream ();
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
            this.baos.write (data, 0, numBytesRead);
        }
    }

    public ByteArrayOutputStream getOutputStream () {
        return this.baos;
    }
}