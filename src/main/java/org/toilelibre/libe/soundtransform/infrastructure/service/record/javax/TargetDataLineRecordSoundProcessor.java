package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

final class TargetDataLineRecordSoundProcessor implements RecordSoundProcessor {

    public enum TargetDataLineRecordSoundProcessorErrorCode implements ErrorCode {

        NOT_READY("Not ready to record a sound"), AUDIO_FORMAT_EXPECTED("An audio format was expected (%1s)"), TARGET_LINE_UNAVAILABLE("Target record line unavailable"), AUDIO_FORMAT_NOT_SUPPORTED("Audio format not supported by AudioSystem");

        private final String messageFormat;

        TargetDataLineRecordSoundProcessorErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private TargetDataLine line;
    private TargetDataLineReaderThread readerThread;

    public TargetDataLineRecordSoundProcessor() {

    }

    @Override
    public InputStream recordRawInputStream(Object audioFormat1, Object stop) throws SoundTransformException {
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED, new IllegalArgumentException());
        }
        AudioFormat audioFormat = (AudioFormat) audioFormat1;

        this.startRecording(audioFormat);
        waitForStop(stop);
        this.stopRecording();
        return new ByteArrayInputStream(this.readerThread.getOutputStream ().toByteArray());
    }

    /**
     * @param stop
     * @throws SoundTransformException
     */

    private void waitForStop(Object stop) throws SoundTransformException {
        boolean stopped = false;
        synchronized (stop) {
            try {
                while (!stopped) {
                    stop.wait();
                    stopped = true;
                }
            } catch (InterruptedException e) {
                throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.NOT_READY, e);
            }
        }
    }

    private void startRecording(AudioFormat audioFormat) throws SoundTransformException {
        // format is an AudioFormat object
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(info)) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_NOT_SUPPORTED, new UnsupportedOperationException(), audioFormat);
        }
        // Obtain and open the line.
        try {
            this.line = (TargetDataLine) AudioSystem.getLine(info);
            this.line.open(audioFormat);
        } catch (LineUnavailableException ex) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.TARGET_LINE_UNAVAILABLE, ex);
        }

        // Begin audio capture.
        this.line.start();
        this.readerThread = new TargetDataLineReaderThread (this.line);
        this.readerThread.start();
    }

    private void stopRecording() {
        // stops the recording activity
        this.readerThread.stopRecording();
        this.line.stop ();
        this.line.close();
    }

}
