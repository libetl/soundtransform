package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordSoundProcessor.AndroidRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

import android.media.AudioRecord;

final class AndroidRecorderThread extends Thread {

    private static final int            ARBITRARY_BUFFER = 1024;
    private static final int            EIGHT            = 8;
    private static final int            TWO              = 2;
    /**
     *
     */
    private final AudioRecord           audioRecord;
    private final ByteArrayOutputStream baos;
    private boolean                     recording;

    /**
     * @param bufferSize
     * @param androidRecordSoundProcessor
     */
    AndroidRecorderThread (final AudioRecord audioRecord1, final int bufferSize) {
        this.audioRecord = audioRecord1;
        this.baos = new ByteArrayOutputStream (bufferSize);

    }

    public ByteArrayOutputStream getOutputStream () {
        return this.baos;
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

        final short [] sData = new short [AndroidRecorderThread.ARBITRARY_BUFFER];

        while (this.recording) {
            // gets the voice output from microphone to byte format
            if (this.audioRecord.getRecordingState () != AudioRecord.STATE_UNINITIALIZED) {
                final int read = this.audioRecord.read (sData, 0, sData.length);
                if (read > 0) {
                    this.baos.write (this.short2byte (sData), 0, read);
                } else if (read == 0) {
                    this.recording = false;
                }
            }
        }
    }
}