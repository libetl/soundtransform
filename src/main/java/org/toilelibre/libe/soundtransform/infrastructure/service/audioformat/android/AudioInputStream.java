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
    
    private byte [] intBuffer    = new byte [4];
    private byte [] shortBuffer  = new byte [2];
    private InputStreamInfo info;
    
    public AudioInputStream (File file) throws IOException {
        super (file);
        this.findMetadata ();
    }


    public InputStreamInfo getInfo () {
        return this.info;
    }

    private String readFourChars () throws IOException{
        this.read (intBuffer);
        return new String (intBuffer);
    }

    private int readShort () throws IOException{
        this.read (shortBuffer);
        return this.byteArrayToInt (shortBuffer);
    }
    private int readInt () throws IOException{
        this.read (intBuffer);
        return this.byteArrayToInt (intBuffer);
    }
    
    private int byteArrayToInt(byte[] b)
    {
        int i = 0;
        for (int j = 0 ; j < b.length ; j++){
           i+= (b [j]) << j * 8; 
        }
        return i;
    }

    private void findMetadata () throws IOException {
        String string = this.readFourChars ();
        if (!"RIFF".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_MAGIC_NUMBER, 
                    new IllegalArgumentException ()));
        }
        //file size
        this.readInt ();
        string = this.readFourChars ();
        if (!"WAVE".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_WAVE_HEADER, 
                    new IllegalArgumentException ()));
        }
        string = this.readFourChars ();
        if (!"fmt ".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_WAVE_HEADER, 
                    new IllegalArgumentException ()));
        }
        //size of chunk
        this.readInt ();
        int typeOfEncoding = this.readShort ();
        if (typeOfEncoding != 1){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NON_PCM_WAV, 
                    new IllegalArgumentException ()));
        }
        int channels = this.readShort ();
        int sampleRate = (this.readInt () + 65536) % 65536;
        //byterate
        this.readInt ();
        int frameSize =  this.readShort ();
        int sampleSize = this.readShort () / 8;
        string = this.readFourChars ();
        if ("LIST".equals (string)){
            int soundInfoSize = this.readInt ();
            this.skip (soundInfoSize);
            string = this.readFourChars ();
        }
        if (!"data".equals (string)){
            throw new SoundTransformRuntimeException (new SoundTransformException (
                    AudioInputStreamErrorCode.NO_DATA_SEPARATOR, 
                    new IllegalArgumentException ()));
        }
        int dataSize = this.readInt ();
        this.info = new InputStreamInfo (channels, dataSize / (frameSize * 8), sampleSize, sampleRate, false, true);
    }


}
