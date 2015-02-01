package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

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

    private static final String RIFF               = "RIFF";
    private static final String WAVE               = "WAVE";
    private static final String FMT_               = "fmt ";
    private static final String LIST               = "LIST";
    private static final String DATA               = "data";
    private static final int    INFO_METADATA_SIZE = 44;

    private static final int    INFO_CHUNK_SIZE    = 16;

    public AndroidWavHelper () {

    }

    @SuppressWarnings ("unused")
    public InputStreamInfo readMetadata (final AudioInputStream ais) throws IOException {
        String string = ais.readFourChars ();
        if (!AndroidWavHelper.RIFF.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_MAGIC_NUMBER, new IllegalArgumentException ()));
        }
        // file size
        final int fileSize = ais.readInt2 ();
        string = ais.readFourChars ();
        if (!AndroidWavHelper.WAVE.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        string = ais.readFourChars ();
        if (!AndroidWavHelper.FMT_.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        // size of chunk
        final int chunkSize = ais.readInt2 ();
        final int typeOfEncoding = ais.readShort2 ();
        if (typeOfEncoding != 1) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NON_PCM_WAV, new IllegalArgumentException ()));
        }
        final int channels = ais.readShort2 ();
        final int sampleRate = (ais.readInt2 () + 65536) % 65536;
        // byterate
        final int byterate = ais.readInt2 ();
        final int frameSize = ais.readShort2 ();
        final int sampleSize = ais.readShort2 ();
        string = ais.readFourChars ();
        int soundInfoSize = 0;
        String list = null;
        if (AndroidWavHelper.LIST.equals (string)) {
            soundInfoSize = ais.readInt2 ();
            final byte [] listByte = new byte [soundInfoSize];
            ais.read (listByte);
            list = new String (listByte);
            string = ais.readFourChars ();
        }
        if (!AndroidWavHelper.DATA.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_DATA_SEPARATOR, new IllegalArgumentException ()));
        }
        final int dataSize = ais.readInt2 ();
        return new InputStreamInfo (channels, dataSize / frameSize, sampleSize / 8, sampleRate, false, true, list);
    }

    public void writeMetadata (final ByteArrayWithAudioFormatInputStream audioInputStream, final WavOutputStream outputStream) throws IOException {
        final InputStreamInfo info = audioInputStream.getInfo ();
        final int soundInfoSize = info.getSoundInfo () == null ? 0 : info.getSoundInfo ().length ();
        final int fileSize = (int) (AndroidWavHelper.INFO_METADATA_SIZE + soundInfoSize + (info.getFrameLength () * info.getSampleSize () * info.getChannels ()));
        final int chunkSize = AndroidWavHelper.INFO_CHUNK_SIZE;
        final int typeOfEncoding = 1;
        final int channels = info.getChannels ();
        final int sampleRate = (int) info.getSampleRate ();
        final int byterate = (int) info.getSampleRate () * info.getSampleSize ();
        final int frameSize = info.getSampleSize () / info.getChannels ();
        final int sampleSize = info.getSampleSize () * 8;
        final int dataSize = (int) info.getFrameLength () * info.getSampleSize ();
        outputStream.write (AndroidWavHelper.RIFF.getBytes ());
        outputStream.writeInt (fileSize);
        outputStream.write (AndroidWavHelper.WAVE.getBytes ());
        outputStream.write (AndroidWavHelper.FMT_.getBytes ());
        outputStream.writeInt (chunkSize);
        outputStream.writeShortInt (typeOfEncoding);
        outputStream.writeShortInt (channels);
        outputStream.writeInt (sampleRate);
        outputStream.writeInt (byterate);
        outputStream.writeShortInt (frameSize);
        outputStream.writeShortInt (sampleSize);
        if (info.getSoundInfo () != null) {
            outputStream.write (AndroidWavHelper.LIST.getBytes ());
            outputStream.writeInt (info.getSoundInfo ().length ());
            outputStream.write (info.getSoundInfo ().getBytes ());
        }
        outputStream.write (AndroidWavHelper.DATA.getBytes ());
        outputStream.writeInt (dataSize);
    }
}
