package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

class TargetDataLineRecordSoundProcessor implements RecordSoundProcessor {

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

    private ByteArrayOutputStream baos;
    private boolean isRecording = false;

    public TargetDataLineRecordSoundProcessor() {

    }

    @Override
    public InputStream recordRawInputStream(Object audioFormat1, Object stop) throws SoundTransformException {
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED, new IllegalArgumentException());
        }
        AudioFormat audioFormat = (AudioFormat) audioFormat1;

        this.startRecording(audioFormat);
        synchronized (stop){
          try {
              stop.wait();
          } catch (InterruptedException e) {
              throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.NOT_READY, e);
          }
        }
        this.stopRecording();
        return new ByteArrayInputStream(this.baos.toByteArray());
    }

    private void startRecording(AudioFormat audioFormat) throws SoundTransformException {
        final TargetDataLine line;
        // format is an AudioFormat object
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        this.baos = new ByteArrayOutputStream();

        if (!AudioSystem.isLineSupported(info)) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_NOT_SUPPORTED, new UnsupportedOperationException(), audioFormat);
        }
        // Obtain and open the line.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
        } catch (LineUnavailableException ex) {
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.TARGET_LINE_UNAVAILABLE, ex);
        }
        this.isRecording = true;

        // Begin audio capture.
        line.start();
        new Thread() {

            public void run() {

                byte[] data = new byte[line.getBufferSize() / 5];
                while (!TargetDataLineRecordSoundProcessor.this.isRecording) {
                    // Read the next chunk of data from the TargetDataLine.
                    final int numBytesRead = line.read(data, 0, data.length);
                    // Save this chunk of data.
                    TargetDataLineRecordSoundProcessor.this.baos.write(data, 0, numBytesRead);
                }
            }
        }.start();
        line.close();
    }

    private void stopRecording() {
        // stops the recording activity
        this.isRecording = false;
    }

}
