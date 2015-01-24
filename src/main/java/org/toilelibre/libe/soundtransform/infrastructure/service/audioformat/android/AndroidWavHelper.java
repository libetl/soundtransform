package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.IOException;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AndroidWavHelper {
    private static final String RIFF = "RIFF";
    private static final String WAVE = "WAVE";
    private static final String FMT_ = "fmt ";
    private static final String LIST = "LIST";
    private static final String DATA = "data";
    private static final int  INFO_METADATA_SIZE = 44;
    private static final int  INFO_CHUNK_SIZE = 16;
    
    public enum AudioWavHelperErrorCode implements ErrorCode {

        NO_MAGIC_NUMBER ("Expected a RIFF magic number"), NO_WAVE_HEADER ("RIFF file but not WAVE"), NOT_UNDERSTANDABLE_WAV ("Wave file was not understood"), NON_PCM_WAV ("Can not understand non PCM WAVE"), NO_DATA_SEPARATOR ("Could not find the data separator");

        private final String messageFormat;

        AudioWavHelperErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public AndroidWavHelper () {

    }

    public InputStreamInfo readMetadata (AudioInputStream ais) throws IOException {
        String string = ais.readFourChars ();
        if (!RIFF.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_MAGIC_NUMBER, new IllegalArgumentException ()));
        }
        // file size
        int fileSize = ais.readInt ();
        string = ais.readFourChars ();
        if (!WAVE.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        string = ais.readFourChars ();
        if (!FMT_.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        // size of chunk
        int chunkSize = ais.readInt ();
        int typeOfEncoding = ais.readShort ();
        if (typeOfEncoding != 1) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NON_PCM_WAV, new IllegalArgumentException ()));
        }
        int channels = ais.readShort ();
        int sampleRate = (ais.readInt () + 65536) % 65536;
        // byterate
        int byterate = ais.readInt ();
        int frameSize = ais.readShort ();
        int sampleSize = ais.readShort () / 8;
        string = ais.readFourChars ();
        int soundInfoSize = 0;
        if (LIST.equals (string)) {
            soundInfoSize = ais.readInt ();
            ais.skip (soundInfoSize);
            string = ais.readFourChars ();
        }
        if (!DATA.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_DATA_SEPARATOR, new IllegalArgumentException ()));
        }
        int dataSize = ais.readInt ();
        return new InputStreamInfo (channels, dataSize / (frameSize), sampleSize, sampleRate, false, true);
    }

    public void writeMetadata (ByteArrayWithAudioFormatInputStream audioInputStream, WavOutputStream outputStream) throws IOException {
        InputStreamInfo info = audioInputStream.getInfo ();
        int fileSize = (int)(INFO_METADATA_SIZE + info.getFrameLength () * info.getSampleSize () * info.getChannels ());
        int chunkSize = INFO_CHUNK_SIZE;
        int typeOfEncoding = 1;
        int channels = info.getChannels ();
        int sampleRate = (int)info.getSampleRate ();
        int byterate = (int)info.getSampleRate () * info.getSampleSize ();
        int frameSize = (int)info.getSampleSize () / info.getChannels ();
        int sampleSize = (int)info.getSampleSize ();
        int dataSize = (int) info.getFrameLength () * info.getSampleSize ();
        outputStream.write (RIFF.getBytes ());
        outputStream.writeInt (fileSize);
        outputStream.write (WAVE.getBytes ());
        outputStream.write (FMT_.getBytes ());
        outputStream.writeInt (chunkSize);
        outputStream.writeShortInt (typeOfEncoding);
        outputStream.writeShortInt (channels);
        outputStream.writeInt (sampleRate);
        outputStream.writeInt (byterate);
        outputStream.writeShortInt (frameSize);
        outputStream.writeShortInt (sampleSize);
        outputStream.write (DATA.getBytes ());
        outputStream.writeInt (dataSize);
    }
}
