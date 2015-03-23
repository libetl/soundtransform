package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordSoundProcessor.AndroidRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

import android.media.AudioRecord;

final class AndroidRecorderThread extends Thread {

    /**
     * 
     */
    private final AudioRecord audioRecord;
    private final ByteArrayOutputStream baos;

    /**
     * @param bufferSize 
     * @param androidRecordSoundProcessor
     */
    AndroidRecorderThread(AudioRecord audioRecord1, int bufferSize) {
        this.audioRecord = audioRecord1;
        this.baos = new ByteArrayOutputStream (bufferSize);

    }

    private boolean recording;

    // convert short to byte
    private byte[] short2byte(final short[] sData) {
        final int shortArrsize = sData.length;
        final byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[i * 2 + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    @Override
    public void run() {
        this.recording = true;
        try {
            this.writeAudioDataToFile();
        } catch (IOException e) {
            throw new SoundTransformRuntimeException(AndroidRecordSoundProcessorErrorCode.NOT_READY, e);
        }
    }

    private void writeAudioDataToFile() throws IOException {
        // Write the output audio in byte

        final short sData[] = new short[1024];

        while (this.recording) {
            // gets the voice output from microphone to byte format
            if (audioRecord.getRecordingState() != AudioRecord.STATE_UNINITIALIZED) {
                final int read = audioRecord.read(sData, 0, sData.length);
                if (read > 0) {
                    this.baos.write(this.short2byte(sData), 0, read);
                } else if (read == 0) {
                    this.recording = false;
                }
            }
        }
    }

    public ByteArrayOutputStream getOutputStream() {
        return this.baos;
    }
}