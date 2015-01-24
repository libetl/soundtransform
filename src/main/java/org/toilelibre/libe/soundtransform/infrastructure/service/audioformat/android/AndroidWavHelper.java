package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.FileOutputStream;
import java.io.IOException;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AndroidWavHelper {
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
        if (!"RIFF".equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_MAGIC_NUMBER, new IllegalArgumentException ()));
        }
        // file size
        ais.readInt ();
        string = ais.readFourChars ();
        if (!"WAVE".equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        string = ais.readFourChars ();
        if (!"fmt ".equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        // size of chunk
        ais.readInt ();
        int typeOfEncoding = ais.readShort ();
        if (typeOfEncoding != 1) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NON_PCM_WAV, new IllegalArgumentException ()));
        }
        int channels = ais.readShort ();
        int sampleRate = (ais.readInt () + 65536) % 65536;
        // byterate
        ais.readInt ();
        int frameSize = ais.readShort ();
        int sampleSize = ais.readShort () / 8;
        string = ais.readFourChars ();
        if ("LIST".equals (string)) {
            int soundInfoSize = ais.readInt ();
            ais.skip (soundInfoSize);
            string = ais.readFourChars ();
        }
        if (!"data".equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_DATA_SEPARATOR, new IllegalArgumentException ()));
        }
        int dataSize = ais.readInt ();
        return new InputStreamInfo (channels, dataSize / (frameSize), sampleSize, sampleRate, false, true);
    }

    public void writeMetadata (ByteArrayWithAudioFormatInputStream audioInputStream, FileOutputStream outputStream) {

    }
}
