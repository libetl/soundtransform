package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map.Entry;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.Obuffer;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

class MP3JLayerConverter implements Converter {

    private static final String NOT_A_MP3_FILE = "Not a mp3 file";

    private static class StreamBuffer extends Obuffer {
        private static final int ONE_FILLED_BYTE = Byte.MAX_VALUE - Byte.MIN_VALUE;
        private final int        nChannels;
        private final byte []    buffer;
        private final int []     bufferPointers;
        private final boolean    bigEndian;

        public StreamBuffer (final int nChannels1, final boolean bigEndian1) {
            this.nChannels = nChannels1;
            this.buffer = new byte [Obuffer.OBUFFERSIZE * nChannels1];
            this.bufferPointers = new int [nChannels1];
            this.reset ();
            this.bigEndian = bigEndian1;
        }

        @Override
        public void append (final int nChannel, final short value) {
            byte firstByte;
            byte secondByte;
            if (this.bigEndian) {
                firstByte = (byte) (value >>> Byte.SIZE & StreamBuffer.ONE_FILLED_BYTE);
                secondByte = (byte) (value & StreamBuffer.ONE_FILLED_BYTE);
            } else {
                firstByte = (byte) (value & StreamBuffer.ONE_FILLED_BYTE);
                secondByte = (byte) (value >>> Byte.SIZE & StreamBuffer.ONE_FILLED_BYTE);
            }
            this.buffer [this.bufferPointers [nChannel] % this.buffer.length] = firstByte;
            this.buffer [ (this.bufferPointers [nChannel] + 1) % this.buffer.length] = secondByte;
            this.bufferPointers [nChannel] += this.nChannels * MP3JLayerConverter.SAMPLE_SIZE;
        }

        @Override
        public void set_stop_flag () {
            // not relevant in this impl (the interface from the JLayer lib may
            // not be clean enough)
        }

        @Override
        public void close () {
            // not relevant in this impl (the interface from the JLayer lib may
            // not be clean enough)
        }

        @Override
        public void write_buffer (final int nValue) {
            // not relevant in this impl (the interface from the JLayer lib may
            // not be clean enough)
        }

        @Override
        public void clear_buffer () {
            Arrays.fill (this.buffer, (byte) 0);
            this.reset ();
        }

        public byte [] getBuffer () {
            return this.buffer;
        }

        public void reset () {
            for (int i = 0 ; i < this.nChannels ; i++) {
                this.bufferPointers [i] = i * MP3JLayerConverter.SAMPLE_SIZE;
            }
        }
    }

    private static final int     MONO        = 1;
    private static final int     STEREO      = 2;
    private static final int     SAMPLE_SIZE = 2;
    private static final boolean BIG_ENDIAN  = false;
    private static final boolean PCM_SIGNED  = true;

    @Override
    public Entry<StreamInfo, ByteArrayOutputStream> convert (final InputStream input) throws SoundTransformException {
        try {
            return this.convert (input, new Decoder.Params ());
        } catch (final JavaLayerException jle) {
            throw new SoundTransformException (AudioFileHelperErrorCode.CUSTOM_CONVERSION_FAILED, jle, "MP3");
        }
    }

    private Entry<StreamInfo, ByteArrayOutputStream> convert (final InputStream sourceStream, final Decoder.Params decoderParams) throws JavaLayerException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
        int channels = 1;
        float sampleRate = 1;
        FormatInfo formatInfo = null;
        StreamBuffer outBuffer = null;
        InputStream realSourceStream = sourceStream;
        try {
            if (! (realSourceStream instanceof BufferedInputStream)) {
                realSourceStream = new BufferedInputStream (realSourceStream);
            }
            if (realSourceStream.markSupported ()) {
                realSourceStream.mark (-1);
                realSourceStream.reset ();
            }

            final Decoder decoder = new Decoder (decoderParams);
            final Bitstream stream = new Bitstream (realSourceStream);

            Header header = stream.readFrame ();
            if (header == null) {
                throw new JavaLayerException (MP3JLayerConverter.NOT_A_MP3_FILE);
            }
            channels = header.mode () == Header.SINGLE_CHANNEL ? MP3JLayerConverter.MONO : MP3JLayerConverter.STEREO;
            sampleRate = header.frequency ();
            outBuffer = new StreamBuffer (channels, MP3JLayerConverter.BIG_ENDIAN);
            formatInfo = new FormatInfo (MP3JLayerConverter.SAMPLE_SIZE, sampleRate);

            decoder.setOutputBuffer (outBuffer);

            while (header != null) {

                decoder.decodeFrame (header, stream);

                outputStream.write (outBuffer.getBuffer ());
                outBuffer.clear_buffer ();

                stream.closeFrame ();
                header = stream.readFrame ();
            }

        } catch (final IOException ex) {
            throw new JavaLayerException (ex.getLocalizedMessage (), ex);
        } finally {

            if (outBuffer != null) {
                outBuffer.close ();
            }
        }
        return new ResultEntry (new StreamInfo (channels, outputStream.size () / Byte.SIZE, formatInfo.getSampleSize (), formatInfo.getSampleRate (), MP3JLayerConverter.BIG_ENDIAN, MP3JLayerConverter.PCM_SIGNED, "Converted from a MP3 file"), outputStream);
    }
}
