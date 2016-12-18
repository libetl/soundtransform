package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.IOException;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordSoundProcessor.AndroidRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;

import android.media.AudioRecord;

final class AndroidRecorderThread extends Thread {

    private static final int                 EIGHT            = 8;
    private static final int                 TWO              = 2;
    private final AudioRecord                audioRecord;
    private boolean                          recording;
    private final BytesExporterFromThread<?> bytesExporter;

    /**
     * @param audioRecord1   the audioRecord object
     * @param bytesExporter1 used exporter
     */
    AndroidRecorderThread (final AudioRecord audioRecord1, final BytesExporterFromThread<?> bytesExporter1) {
        this.audioRecord = audioRecord1;
        this.bytesExporter = bytesExporter1;
        this.setName (this.getClass ().getSimpleName ());
    }

    @Override
    public void run () {
        this.recording = true;
        try {
            this.writeAudioDataToFile ();
        } catch (final IOException e) {
            throw new SoundTransformRuntimeException (AndroidRecordSoundProcessorErrorCode.NOT_READY, e);
        }
    }

    // convert short to byte
    private byte [] short2byte (final short [] sData) {
        final int shortArrsize = sData.length;
        final byte [] bytes = new byte [shortArrsize * AndroidRecorderThread.TWO];
        for (int i = 0 ; i < shortArrsize ; i++) {
            bytes [i * AndroidRecorderThread.TWO] = (byte) (sData [i] & 0x00FF);
            bytes [i * AndroidRecorderThread.TWO + 1] = (byte) (sData [i] >> AndroidRecorderThread.EIGHT);
            sData [i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile () throws IOException {
        // Write the output audio in byte

        final short [] sData = new short [this.bytesExporter.getReportedBufferSize() / AndroidRecorderThread.TWO];

        while (this.recording) {
            // gets the voice output from microphone to byte format
            if (this.audioRecord.getRecordingState () != AudioRecord.STATE_UNINITIALIZED) {
                final int read = this.audioRecord.read (sData, 0, sData.length);
                if (read > 0) {
                    this.bytesExporter.export (this.short2byte (sData), read * AndroidRecorderThread.TWO);
                } else if (read == 0) {
                    this.recording = false;
                }
            }
        }
    }

    void stopRecording () {
        this.recording = false;
    }
}