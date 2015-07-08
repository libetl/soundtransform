package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter.OutputAsByteArrayOutputStream;
import org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter.OutputAsByteBuffer;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

final class AndroidRecordSoundProcessor extends AbstractLogAware<AndroidRecordSoundProcessor> implements RecordSoundProcessor {

    public enum AndroidRecordSoundProcessorEvent implements EventCode {

        NOT_ABLE_TO_READ (LogLevel.ERROR, "Not able to read the recorded data");

        private final String   messageFormat;
        private final LogLevel logLevel;

        AndroidRecordSoundProcessorEvent (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }
    }

    public enum AndroidRecordSoundProcessorErrorCode implements ErrorCode {

        NOT_READY ("Not ready to record a sound"), STREAM_INFO_NOT_SUPPORTED ("Stream Info not supported by Recorder"), STREAM_INFO_EXPECTED ("A stream info was expected");

        private final String messageFormat;

        AndroidRecordSoundProcessorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private int                   bufferSize;
    private AudioRecord           recorder;

    private AndroidRecorderThread recordingThread;
    private OutputAsByteArrayOutputStream bytesExporter;

    public AudioRecord findAudioRecorder (final StreamInfo streamInfo) throws SoundTransformException {
        final int audioFormat = streamInfo.getSampleSize () == 1 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;
        final int channelConfig = streamInfo.getChannels () == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        final int rate = (int) streamInfo.getSampleRate ();
        this.bufferSize = AudioRecord.getMinBufferSize (rate, channelConfig, audioFormat);
        final AudioRecord candidateRecorder = new AudioRecord (AudioSource.DEFAULT, rate, channelConfig, audioFormat, this.bufferSize);

        if (this.bufferSize != AudioRecord.ERROR_BAD_VALUE && candidateRecorder.getState () == AudioRecord.STATE_INITIALIZED) {
            // check if we can instantiate and have a success
            return candidateRecorder;
        }

        throw new SoundTransformException (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_NOT_SUPPORTED, new UnsupportedOperationException (), streamInfo);
    }

    @Override
    public InputStream recordRawInputStream (final Object streamInfo1, final Object stop) throws SoundTransformException {
        if (!(streamInfo1 instanceof StreamInfo)) {
            throw new SoundTransformException (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_EXPECTED, new IllegalArgumentException ());
        }
        final StreamInfo streamInfo = (StreamInfo) streamInfo1;
        this.startRecording (streamInfo);
        this.waitForStop (stop);
        this.stopRecording ();
        return new ByteArrayInputStream (this.bytesExporter.getOutput ().toByteArray ());
    }

    private void waitForStop (final Object stop) throws SoundTransformException {
        boolean stopped = false;
        synchronized (stop) {
            try {
                while (!stopped) {
                    stop.wait ();
                    stopped = true;
                }
            } catch (final InterruptedException e) {
                throw new SoundTransformException (AndroidRecordSoundProcessorErrorCode.NOT_READY, e);
            }
        }
    }

    private void startRecording (final StreamInfo streamInfo) throws SoundTransformException {

        this.recorder = this.findAudioRecorder (streamInfo);
        this.bytesExporter = $.select (OutputAsByteArrayOutputStream.class);
        this.recordingThread = new AndroidRecorderThread (this.recorder, this.bufferSize, bytesExporter);
        this.bytesExporter.init (this.bufferSize);
        this.recorder.startRecording ();
        this.recordingThread.start ();
    }

    private void stopRecording () {
        // stops the recording activity
        if (this.recorder != null) {
            this.recorder.stop ();
            this.recorder.release ();
        }
    }

    @Override
    public ByteBuffer startRecordingAndReturnByteBuffer (final Object audioFormat, final Object stop) throws SoundTransformException {
        if (!(audioFormat instanceof StreamInfo)) {
            throw new SoundTransformException (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_EXPECTED, new IllegalArgumentException ());
        }
        final StreamInfo streamInfo = (StreamInfo) audioFormat;
        this.recorder = this.findAudioRecorder (streamInfo);
        OutputAsByteBuffer bytesExporter = $.select (OutputAsByteBuffer.class);
        bytesExporter.init (this.bufferSize);
        this.recordingThread = new AndroidRecorderThread (this.recorder, this.bufferSize, bytesExporter);
        this.recorder.startRecording ();
        this.recordingThread.start ();
        new Thread () {
            public void run () {
                try {
                    AndroidRecordSoundProcessor.this.waitForStop (stop);
                    AndroidRecordSoundProcessor.this.stopRecording ();
                } catch (SoundTransformException soundTransformException) {
                    throw new SoundTransformRuntimeException (soundTransformException);
                }
            }
        }.start ();
        return bytesExporter.getOutput ();
    }
}
