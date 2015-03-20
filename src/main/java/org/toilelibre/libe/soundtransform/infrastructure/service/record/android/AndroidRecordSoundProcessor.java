package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

class AndroidRecordSoundProcessor extends AbstractLogAware<AndroidRecordSoundProcessor> implements RecordSoundProcessor {

    @Override
    public InputStream record (Object stop) {
        // TODO Auto-generated method stub
        return null;
    }

    
    private boolean                    isRecording             = false;

    private ByteArrayOutputStream baos;
    private int bufferSize;
    private int bytesPerElement;

    private AudioRecord recorder;

    private Thread recordingThread;


    //convert short to byte
    private byte [] short2byte (short [] sData) {
        final int shortArrsize = sData.length;
        final byte [] bytes = new byte [shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes [i * 2] = (byte) (sData [i] & 0x00FF);
            bytes [ (i * 2) + 1] = (byte) (sData [i] >> 8);
            sData [i] = 0;
        }
        return bytes;

    }

    private static int[] SAMPLE_RATES = new int[] { 8000, 11025, 22050, 44100 };
    
    public AudioRecord findAudioRecorder() {
        for (int rate : SAMPLE_RATES) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        this.bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (this.bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    private void startRecording () {

        this.baos = new ByteArrayOutputStream(this.bufferSize);
        this.recorder = findAudioRecorder();
        this.bytesPerElement = this.recorder.getAudioFormat () == AudioFormat.ENCODING_PCM_8BIT ? 1 : 2;
        this.isRecording = true;
        this.recordingThread = new Thread (new Runnable () {
            @Override
            public void run () {
                try {
                    AndroidRecordSoundProcessor.this.writeAudioDataToFile ();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "AudioRecorder Thread");
        this.recorder.startRecording ();
        this.recordingThread.start ();
    }

    private void stopRecording () {
        // stops the recording activity
        if (null != this.recorder) {
            this.isRecording = false;
            this.recorder.stop ();
            this.recorder.release ();
            StreamInfo si = new StreamInfo (this.recorder.getChannelConfiguration () ==  AudioFormat.CHANNEL_IN_MONO ? 1 : 2, this.baos.size () / this.bytesPerElement, this.bytesPerElement, this.recorder.getSampleRate (), false, true, null);
            ByteArrayInputStream baos = new ByteArrayInputStream (this.baos.toByteArray ());
            
            this.recorder = null;
            this.recordingThread = null;
        }
    }

    private void writeAudioDataToFile () throws IOException {
        // Write the output audio in byte

        final short sData [] = new short [1024];

        while (this.isRecording) {
            // gets the voice output from microphone to byte format
            if (this.recorder.getRecordingState () != AudioRecord.STATE_UNINITIALIZED){
                int read = this.recorder.read (sData, 0, sData.length);
                if (read > 0){
                    baos.write (this.short2byte (sData), 0, read);
                }else if (read == 0) {
                    this.isRecording = false;
                }
            }
        }
    }

}
