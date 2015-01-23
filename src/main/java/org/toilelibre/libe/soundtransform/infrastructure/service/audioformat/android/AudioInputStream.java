package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioInputStream extends FileInputStream {
    public enum AudioInputStreamErrorCode implements ErrorCode {

        NO_MAGIC_NUMBER ("Expected a RIFF magic number"),
        NO_WAVE_HEADER ("RIFF file but not WAVE"),
        NOT_UNDERSTANDABLE_WAV ("Wave file was not understood"),
        NON_PCM_WAV ("Can not understand non PCM WAVE"),
        NO_DATA_SEPARATOR ("Could not find the data separator");

        private final String messageFormat;

        AudioInputStreamErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }
    
    private InputStreamInfo info;
    
    public AudioInputStream (File file) throws IOException {
        super (file);
        this.findMetadata ();
    }


    public InputStreamInfo getInfo () {
        return this.info;
    }

    private int byteArrayToInt(byte[] b)
    {
        int i = 0;
        for (int j = 0 ; j < b.length ; j++){
           i+= (b [j]) * Math.pow (256, j); 
        }
        return i;
    }

    private void findMetadata () throws IOException {
        byte [] buffer = new byte [4];
        byte [] shortBuffer = new byte [2];
        this.read (buffer);
        String string = new String (buffer);
        if (!"RIFF".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_MAGIC_NUMBER, 
                    new IllegalArgumentException ()));
        }
        this.read (buffer);
        //int fileSize = 
                this.byteArrayToInt (buffer);
        this.read (buffer);
        string = new String (buffer);
        if (!"WAVE".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_WAVE_HEADER, 
                    new IllegalArgumentException ()));
        }
        this.read (buffer);
        string = new String (buffer);
        if (!"fmt ".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_WAVE_HEADER, 
                    new IllegalArgumentException ()));
        }
        this.read (buffer);
        //int sizeOfChunk = 
                this.byteArrayToInt (buffer);
        this.read (shortBuffer);
        int typeOfEncoding = this.byteArrayToInt (shortBuffer);
        if (typeOfEncoding != 1){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NON_PCM_WAV, 
                    new IllegalArgumentException ()));
        }
        this.read (shortBuffer);
        int channels = this.byteArrayToInt (shortBuffer);
        this.read (buffer);
        int sampleRate = (this.byteArrayToInt (buffer) + 65536) % 65536;
        this.read (buffer);
        //int byteRate = 
                this.byteArrayToInt (buffer);
        this.read (shortBuffer);
        int frameSize = this.byteArrayToInt (shortBuffer);
        this.read (shortBuffer);
        int sampleSize = this.byteArrayToInt (shortBuffer) / 8;
        this.read (buffer);
        string = new String (buffer);
        if ("LIST".equals (string)){
            this.read (buffer);
            int soundInfoSize = this.byteArrayToInt (buffer);
            this.skip (soundInfoSize);
            this.read (buffer);
            string = new String (buffer);
        }
        if (!"data".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_DATA_SEPARATOR, 
                    new IllegalArgumentException ()));
        }
        this.read (buffer);
        int dataSize = this.byteArrayToInt (buffer);
        this.info = new InputStreamInfo (channels, dataSize / (frameSize * 8), sampleSize, sampleRate, false, true);
    }


}
