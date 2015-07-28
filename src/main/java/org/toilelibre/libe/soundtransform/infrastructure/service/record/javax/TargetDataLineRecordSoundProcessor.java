package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;
import org.toilelibre.libe.soundtransform.model.record.exporter.BytesExporterFromThread;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteArrayOutputStream;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteBuffer;

final class TargetDataLineRecordSoundProcessor implements RecordSoundProcessor {

    public enum TargetDataLineRecordSoundProcessorErrorCode implements ErrorCode {

        NOT_READY ("Not ready to record a sound"), AUDIO_FORMAT_EXPECTED ("An audio format was expected (%1s)"), TARGET_LINE_UNAVAILABLE ("Target record line unavailable"), AUDIO_FORMAT_NOT_SUPPORTED ("Audio format not supported by AudioSystem");

        private final String messageFormat;

        TargetDataLineRecordSoundProcessorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final int           DEFAULT_BUFFER_SIZE      = 32;

    private static final int           DEFAULT_BYTE_BUFFER_SIZE = 16384;

    private TargetDataLine             line;
    private TargetDataLineReaderThread readerThread;

    public TargetDataLineRecordSoundProcessor () {

    }

    @Override
    public InputStream recordRawInputStream (final Object audioFormat1, final Object stop) throws SoundTransformException {
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED, new IllegalArgumentException ());
        }
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;

        final OutputAsByteArrayOutputStream bytesExporter = $.select (OutputAsByteArrayOutputStream.class);
        bytesExporter.init (TargetDataLineRecordSoundProcessor.DEFAULT_BUFFER_SIZE);
        this.startRecording (audioFormat, bytesExporter);
        this.stopProperly(stop);
        return new ByteArrayInputStream (bytesExporter.getOutput ().toByteArray ());
    }

    /**
     * @param stop
     * @throws SoundTransformException
     */
    @Override
    public void stopProperly (final Object stop) throws SoundTransformException {
        this.waitForStop (stop);
        this.stopRecording ();
    }

    /**
     * @param stop
     * @throws SoundTransformException
     */

    private void waitForStop (final Object stop) throws SoundTransformException {
        boolean stopped = false;
        synchronized (stop) {
            try {
                while (!stopped) {
                    stop.wait ();
                    stopped = true;
                }
            } catch (final InterruptedException e) {
                throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.NOT_READY, e);
            }
        }
    }

    private <T> void startRecording (final AudioFormat audioFormat, final BytesExporterFromThread<T> exporter) throws SoundTransformException {
        // format is an AudioFormat object
        final DataLine.Info info = new DataLine.Info (TargetDataLine.class, audioFormat);

        if (!this.checkLineSupported (info)) {
            throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_NOT_SUPPORTED, new UnsupportedOperationException (), audioFormat);
        }

        // Obtain and open the line.
        this.line = this.getDataLine (info);
        try {
            this.line.open (audioFormat);
        } catch (final LineUnavailableException ex) {
            throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.TARGET_LINE_UNAVAILABLE, ex);
        }

        // Begin audio capture.
        this.line.start ();
        this.readerThread = new TargetDataLineReaderThread (this.line, exporter);
        this.readerThread.start ();
    }

    private boolean checkLineSupported (final Info info) {
        return AudioSystem.isLineSupported (info);
    }

    private TargetDataLine getDataLine (final Info info) throws SoundTransformException {
        try {
            return (TargetDataLine) AudioSystem.getLine (info);
        } catch (final LineUnavailableException ex) {
            throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.TARGET_LINE_UNAVAILABLE, ex);
        }
    }

    private void stopRecording () {
        // stops the recording activity
        this.readerThread.stopRecording ();
        this.line.stop ();
        this.line.close ();
    }

    @Override
    public ByteBuffer startRecordingAndReturnByteBuffer (final Object audioFormat1, final Object stop) throws SoundTransformException {
        final RecordSoundProcessor processor = this;
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException (TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED, new IllegalArgumentException ());
        }
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;

        final OutputAsByteBuffer bytesExporter = $.select (OutputAsByteBuffer.class);
        bytesExporter.init (TargetDataLineRecordSoundProcessor.DEFAULT_BYTE_BUFFER_SIZE);
        this.startRecording (audioFormat, bytesExporter);

        new Thread () {
            @Override
            public void run () {
                try {
                    processor.stopProperly (stop);
                } catch (final SoundTransformException soundTransformException) {
                    throw new SoundTransformRuntimeException (soundTransformException);
                }
            }
        }.start ();
        return bytesExporter.getOutput ();
    }
}
