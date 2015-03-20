package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

class AndroidRecordSoundProcessor extends AbstractLogAware<AndroidRecordSoundProcessor> implements RecordSoundProcessor {

    public enum AndroidRecordSoundProcessorErrorCode implements ErrorCode {

        NOT_READY ("Not ready to record a sound"),
        STREAM_INFO_NOT_SUPPORTED ("Stream Info not supported by Recorder"),
        STREAM_INFO_EXPECTED ("A stream info was expected");

        private final String messageFormat;

        AndroidRecordSoundProcessorErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private boolean isRecording = false;

    private ByteArrayOutputStream baos;

    private int bufferSize;
    private AudioRecord recorder;

    private Thread recordingThread;

    public AudioRecord findAudioRecorder(StreamInfo streamInfo) throws SoundTransformException {
        final int audioFormat = streamInfo.getSampleSize() == 1 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;
        final int channelConfig = streamInfo.getChannels() == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        final int rate = (int) streamInfo.getSampleRate();
        this.bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
        final AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, this.bufferSize);

        if (this.bufferSize != AudioRecord.ERROR_BAD_VALUE) {
            // check if we can instantiate and have a success

            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                return recorder;
            }
        }

        throw new SoundTransformException (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_NOT_SUPPORTED, 
                new UnsupportedOperationException(), streamInfo);
    }

    @Override
    public InputStream recordRawInputStream(final Object streamInfo1, final Object stop) throws SoundTransformException {
        if (!(streamInfo1 instanceof StreamInfo)){
            throw new SoundTransformException(AndroidRecordSoundProcessorErrorCode.STREAM_INFO_EXPECTED, new IllegalArgumentException());
        }
        final StreamInfo streamInfo = (StreamInfo) streamInfo1;
        this.startRecording(streamInfo);
        try {
            stop.wait();
        } catch (InterruptedException e) {
            throw new SoundTransformException(AndroidRecordSoundProcessorErrorCode.NOT_READY, e);
        }
        this.stopRecording();
        return new ByteArrayInputStream(this.baos.toByteArray());
    }

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

    private void writeAudioDataToFile() throws IOException {
        // Write the output audio in byte

        final short sData[] = new short[1024];

        while (this.isRecording) {
            // gets the voice output from microphone to byte format
            if (this.recorder.getRecordingState() != AudioRecord.STATE_UNINITIALIZED) {
                final int read = this.recorder.read(sData, 0, sData.length);
                if (read > 0) {
                    this.baos.write(this.short2byte(sData), 0, read);
                } else if (read == 0) {
                    this.isRecording = false;
                }
            }
        }
    }

    private void startRecording(StreamInfo streamInfo) throws SoundTransformException {

        this.baos = new ByteArrayOutputStream(this.bufferSize);
        this.recorder = findAudioRecorder(streamInfo);
        this.isRecording = true;
        this.recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AndroidRecordSoundProcessor.this.writeAudioDataToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "AudioRecorder Thread");
        this.recorder.startRecording();
        this.recordingThread.start();
    }

    private void stopRecording() {
        // stops the recording activity
        if (this.recorder != null) {
            this.isRecording = false;
            this.recorder.stop();
            this.recorder.release();
        }
    }
}
