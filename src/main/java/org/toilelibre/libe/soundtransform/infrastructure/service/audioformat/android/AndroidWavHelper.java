package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.IOException;
import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

final class AndroidWavHelper extends AbstractLogAware<AndroidWavHelper> {
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

    public enum AudioWavHelperEventCode implements EventCode {
        WAV_LIST_INFO_SIZE (LogLevel.PARANOIAC, "Wav list info size in bits : %1d"), READING_A_TECHNICAL_INSTRUMENT (LogLevel.VERBOSE, "%1s, reading a technical instrument : %2s"), TECHNICAL_INSTRUMENT_DOES_NOT_EXIST (LogLevel.WARN, "%1s, the technical instrument : %2s does not exist");

        private final String   messageFormat;
        private final LogLevel logLevel;

        AudioWavHelperEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final String RIFF                = "RIFF";
    private static final String WAVE                = "WAVE";
    private static final String FMT                 = "fmt ";
    private static final String LIST                = "LIST";
    private static final String DATA                = "data";
    private static final int    INFO_METADATA_SIZE  = 44;

    private static final int    INFO_CHUNK_SIZE     = 16;

    private static final int    TWO_BYTES_NB_VALUES = 1 << 2 * Byte.SIZE;
    
    private static final int    WORD_ALIGN_LENGTH   = 4;

    public AndroidWavHelper () {

    }

    private void readMagicChars (final AudioInputStream ais) throws IOException {
        String string = ais.readFourChars ();
        if (!AndroidWavHelper.RIFF.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_MAGIC_NUMBER, new IllegalArgumentException ()));
        }
        ais.readInt2 ();
        string = ais.readFourChars ();
        if (!AndroidWavHelper.WAVE.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        string = ais.readFourChars ();
        if (!AndroidWavHelper.FMT.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_WAVE_HEADER, new IllegalArgumentException ()));
        }
        ais.readInt2 ();

    }

    public StreamInfo readMetadata (final AudioInputStream ais) throws IOException {
        this.readMagicChars (ais);
        final int typeOfEncoding = ais.readShort2 ();
        if (typeOfEncoding != 1) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NON_PCM_WAV, new IllegalArgumentException ()));
        }
        final int channels = ais.readShort2 ();
        final int sampleRate = (ais.readInt2 () + AndroidWavHelper.TWO_BYTES_NB_VALUES) % AndroidWavHelper.TWO_BYTES_NB_VALUES;
        ais.readInt2 ();
        final int frameSize = ais.readShort2 ();
        final int sampleSize = ais.readShort2 ();
        String string = ais.readFourChars ();
        int otherInfosSize = 0;
        String list = null;
        if (AndroidWavHelper.LIST.equals (string)) {
            otherInfosSize = ais.readInt2 ();
            final byte [] listByte = new byte [otherInfosSize];
            this.log (new LogEvent (AudioWavHelperEventCode.WAV_LIST_INFO_SIZE, ais.read (listByte)));
            list = new String (listByte, AudioInputStream.DEFAULT_CHARSET_NAME);
            string = ais.readFourChars ();
        }
        if (!AndroidWavHelper.DATA.equals (string)) {
            throw new SoundTransformRuntimeException (new SoundTransformException (AudioWavHelperErrorCode.NO_DATA_SEPARATOR, new IllegalArgumentException ()));
        }
        final int dataSize = ais.readInt2 ();
        return new StreamInfo (channels, dataSize / frameSize, sampleSize / Byte.SIZE, sampleRate, false, true, list);
    }

    public void writeMetadata (final ByteArrayWithAudioFormatInputStream audioInputStream, final WavOutputStream outputStream) throws IOException {
        this.writeMetadata (audioInputStream.getInfo (), outputStream);
    }
    
    public void writeMetadata (final StreamInfo info, final WavOutputStream outputStream) throws IOException {
        final int otherInfosSize = info.getTaggedInfo () == null ? 0 : info.getTaggedInfo ().length ();
        final int fileSize = (int) (AndroidWavHelper.INFO_METADATA_SIZE + otherInfosSize + info.getFrameLength () * info.getSampleSize () * info.getChannels ());
        final int chunkSize = AndroidWavHelper.INFO_CHUNK_SIZE;
        final int typeOfEncoding = 1;
        final int channels = info.getChannels ();
        final int sampleRate = (int) info.getSampleRate ();
        final int byterate = (int) info.getSampleRate () * info.getSampleSize ();
        final int frameSize = info.getSampleSize () / info.getChannels ();
        final int sampleSize = info.getSampleSize () * Byte.SIZE;
        final int dataSize = (int) info.getFrameLength () * info.getSampleSize ();
        outputStream.write (AndroidWavHelper.RIFF.getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
        outputStream.writeInt (fileSize);
        outputStream.write (AndroidWavHelper.WAVE.getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
        outputStream.write (AndroidWavHelper.FMT.getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
        outputStream.writeInt (chunkSize);
        outputStream.writeShortInt (typeOfEncoding);
        outputStream.writeShortInt (channels);
        outputStream.writeInt (sampleRate);
        outputStream.writeInt (byterate);
        outputStream.writeShortInt (frameSize);
        outputStream.writeShortInt (sampleSize);
        if (info.getTaggedInfo () != null) {
            final int complementaryLength = AndroidWavHelper.WORD_ALIGN_LENGTH - 1 - (info.getTaggedInfo ().length () - 1) % AndroidWavHelper.WORD_ALIGN_LENGTH;
            final char [] complementaryCharArray = new char [complementaryLength];
            Arrays.fill (new char [complementaryLength], ' ');
            outputStream.write (AndroidWavHelper.LIST.getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
            outputStream.writeInt (info.getTaggedInfo ().length () + complementaryLength);
            outputStream.write ((info.getTaggedInfo () + new String (complementaryCharArray)).getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
        }
        outputStream.write (AndroidWavHelper.DATA.getBytes (AudioInputStream.DEFAULT_CHARSET_NAME));
        outputStream.writeInt (dataSize);
    }
}
