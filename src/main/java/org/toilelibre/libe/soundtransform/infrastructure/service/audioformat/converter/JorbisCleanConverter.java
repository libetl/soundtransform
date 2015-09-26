package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperEventCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

class JorbisCleanConverter extends AbstractLogAware<JorbisCleanConverter> implements Converter {
    private static final int HEADERS_NB_STEPS        = 4;
    private static final int BUFFER_SIZE             = (int) Math.pow (2, 11);
    private static final int ERROR_WHILE_READING     = -1;
    private static final int PARTIAL_READ            = 0;
    private static final int PAGE_READ               = 1;
    private static final int SAMPLE_SIZE             = 2;
    private static final int ADDITIONAL_HEADER1_STEP = 2;
    private static final int ADDITIONAL_HEADER2_STEP = 3;

    static class JoggData {
        final Packet      packet      = new Packet ();
        final Page        page        = new Page ();
        final StreamState streamState = new StreamState ();
        final SyncState   syncState   = new SyncState ();
    }

    static class JorbisData {
        final DspState dspState = new DspState ();
        final Block    block    = new Block (this.dspState);
        final Comment  comment  = new Comment ();
        final Info     info     = new Info ();
    }

    static class PcmData {
        /*
         * We need a buffer, it's size, a count to know how many bytes we have
         * read and an index to keep track of where we are. This is standard
         * networking stuff used with read().
         */
        byte []               buffer = null;
        int                   count  = 0;
        int                   index  = 0;

        // A three-dimensional an array with PCM information.
        float [][][]          pcmInfo;

        // The index for the PCM information.
        int []                pcmIndex;
        int                   convertedBufferSize;
        byte []               convertedBuffer;
        ByteArrayOutputStream baos;
    }

    static class ConverterData {
        final JorbisData jorbisData = new JorbisData ();
        final JoggData   joggData   = new JoggData ();
        final PcmData    pcmData    = new PcmData ();
    }

    static class ResultEntry implements Entry<StreamInfo, ByteArrayOutputStream> {

        private final StreamInfo            streamInfo;
        private final ByteArrayOutputStream outputStream;

        public ResultEntry (final StreamInfo streamInfo1, final ByteArrayOutputStream outputStream1) {
            this.streamInfo = streamInfo1;
            this.outputStream = outputStream1;
        }

        @Override
        public StreamInfo getKey () {
            return this.streamInfo;
        }

        @Override
        public ByteArrayOutputStream getValue () {
            return this.outputStream;
        }

        @Override
        public ByteArrayOutputStream setValue (final ByteArrayOutputStream object) {
            throw new UnsupportedOperationException ();
        }

    }

    public ByteArrayOutputStream getOutputStream (final ConverterData converterData) {
        return converterData.pcmData.baos;
    }

    public StreamInfo getStreamInfo (final ConverterData converterData) {
        return new StreamInfo (converterData.jorbisData.info.channels, converterData.pcmData.baos == null ? 0 : (int) (converterData.pcmData.baos.size () * 1.0 * converterData.jorbisData.info.channels / Byte.SIZE), JorbisCleanConverter.SAMPLE_SIZE, converterData.jorbisData.info.rate, false, true,
                "Converted from OGG Vorbis.");
    }

    /**
     * This method is probably easiest understood by looking at the body.
     * However, it will - if no problems occur - call methods to initialize the
     * JOgg JOrbis libraries, read the header, initialize the sound system, read
     * the body of the stream and clean up.
     *
     * @return
     * @throws JorbisReadException
     */
    public ConverterData run (final InputStream oggInputStream) throws JorbisReadException {
        // Check that we got an oggInputStream.
        if (oggInputStream == null) {
            throw new JorbisReadException ("We don't have an input stream and therefore cannot continue.");
        }
        final ConverterData converterData = new ConverterData ();

        // Initialize JOrbis.
        this.initializeJorbis (converterData);

        /*
         * If we can read the header, we try to inialize the sound system. If we
         * could initialize the sound system, we try to read the body.
         */
        if (this.readHeader (converterData, oggInputStream)) {
            this.initializeSound (converterData);
            this.readBody (converterData, oggInputStream);
        }

        // Afterwards, we clean up.
        this.cleanUp (converterData, oggInputStream);

        // return conversion data
        return converterData;
    }

    private void initializeJorbis (final ConverterData converterData) {

        // Initialize SyncState
        converterData.joggData.syncState.init ();

        // Prepare the to SyncState internal buffer
        converterData.joggData.syncState.buffer (JorbisCleanConverter.BUFFER_SIZE);

        /*
         * Fill the buffer with the data from SyncState's internal buffer. Note
         * how the size of this new buffer is different from bufferSize.
         */
        converterData.pcmData.buffer = converterData.joggData.syncState.data;

    }

    private boolean readHeader (final ConverterData converterData, final InputStream oggInputStream) throws JorbisReadException {

        /*
         * We will read the first three packets of the header. We start off by
         * defining packet = 1 and increment that value whenever we have
         * successfully read another packet.
         */
        int step = 1;

        /*
         * While we need more data (which we do until we have read the three
         * header packets), this loop reads from the stream and has a big
         * <code>switch</code> statement which does what it's supposed to do in
         * regards to the current packet.
         */
        while (step < JorbisCleanConverter.HEADERS_NB_STEPS) {
            // Read from the oggInputStream.
            try {
                converterData.pcmData.count = oggInputStream.read (converterData.pcmData.buffer, converterData.pcmData.index, JorbisCleanConverter.BUFFER_SIZE);
            } catch (final IOException exception) {
                throw new JorbisReadException ("Could not read from the input stream.", exception);
            }

            // We let SyncState know how many bytes we read.
            converterData.joggData.syncState.wrote (converterData.pcmData.count);

            if (this.readNextPageAndDecideIfStepShouldBeIncreased (converterData, step)) {
                step++;
            }
        }

        return true;
    }

    private boolean readNextPageAndDecideIfStepShouldBeIncreased (final ConverterData converterData, final int step) throws JorbisReadException {
        /*
         * We want to read the first three packets. For the first packet, we
         * need to initialize the StreamState object and a couple of other
         * things. For packet two and three, the procedure is the same: we take
         * out a page, and then we take out the packet.
         */
        final int status = converterData.joggData.syncState.pageout (converterData.joggData.page);

        switch (status) {
            case ERROR_WHILE_READING:
                throw new JorbisReadException ("Error while reading packet " + step);
            case PARTIAL_READ:
                this.updatePcmDataHeaders (converterData);
                this.ensurePcmDataCountIsNotZero (converterData);
                break;
            case PAGE_READ:
                this.handleNewCompleteHeaderPage (step, converterData);
                this.updatePcmDataHeaders (converterData);
                return true;
            default:
                throw new JorbisReadException ("Unknown status while reading at step " + step);
        }
        return false;
    }

    private void ensurePcmDataCountIsNotZero (final ConverterData converterData) throws JorbisReadException {
        if (converterData.pcmData.count == 0) {
            throw new JorbisReadException ("Not enough header data recieved");
        }
    }

    private void handleNewCompleteHeaderPage (final int step, final ConverterData converterData) throws JorbisReadException {
        if (step == 1) {
            this.initConverterDataFromPage (converterData);
        } else if (step == ADDITIONAL_HEADER1_STEP || step == ADDITIONAL_HEADER2_STEP) {
            this.readAdditionalHeaderFromPage (converterData);
        }

    }

    private void readAdditionalHeaderFromPage (final ConverterData converterData) throws JorbisReadException {
        // Share the page with the StreamState object.
        converterData.joggData.streamState.pagein (converterData.joggData.page);

        /*
         * Reading a page below.
         */
        switch (converterData.joggData.streamState.packetout (converterData.joggData.packet)) {
            // If there is a hole in the data, we must exit.
            case ERROR_WHILE_READING:
                throw new JorbisReadException ("There is a hole in the first packet data.");
            // We got a packet, let's process it.
            case PAGE_READ:
                converterData.jorbisData.info.synthesis_headerin (converterData.jorbisData.comment, converterData.joggData.packet);
                break;
            default:
                break;
        }

    }

    private void initConverterDataFromPage (final ConverterData converterData) throws JorbisReadException {
        // Initializes and resets StreamState.
        converterData.joggData.streamState.init (converterData.joggData.page.serialno ());
        converterData.joggData.streamState.reset ();

        // Initializes the Info and Comment objects.
        converterData.jorbisData.info.init ();
        converterData.jorbisData.comment.init ();

        // Check the page (serial number and stuff).
        if (converterData.joggData.streamState.pagein (converterData.joggData.page) == -1) {
            throw new JorbisReadException ("We got an error while reading the first header page.");
        }

        /*
         * Try to extract a packet. All other return values than "1" indicates
         * there's something wrong.
         */
        if (converterData.joggData.streamState.packetout (converterData.joggData.packet) != 1) {
            throw new JorbisReadException ("We got an error while reading the first header packet.");
        }

        /*
         * We give the packet to the Info object, so that it can extract the
         * Comment-related information, among other things. If this fails, it's
         * not Vorbis data.
         */
        if (converterData.jorbisData.info.synthesis_headerin (converterData.jorbisData.comment, converterData.joggData.packet) < 0) {
            throw new JorbisReadException ("We got an error while interpreting the first packet. Apparantly, it's not Vorbis data.");
        }

    }

    private void updatePcmDataHeaders (final ConverterData converterData) {

        // We get the new index and an updated buffer.
        converterData.pcmData.index = converterData.joggData.syncState.buffer (JorbisCleanConverter.BUFFER_SIZE);
        converterData.pcmData.buffer = converterData.joggData.syncState.data;

    }

    /**
     * This method starts the sound system. It starts with initializing the
     * <code>DspState</code> object, after which it sets up the
     * <code>Block</code> object. Last but not least, it opens a line to the
     * source data line.
     *
     */
    private void initializeSound (final ConverterData converterData) {

        // This buffer is used by the decoding method.
        converterData.pcmData.convertedBufferSize = JorbisCleanConverter.BUFFER_SIZE * SAMPLE_SIZE;
        converterData.pcmData.convertedBuffer = new byte [converterData.pcmData.convertedBufferSize];

        // Initializes the DSP synthesis.
        converterData.jorbisData.dspState.synthesis_init (converterData.jorbisData.info);

        // Make the Block object aware of the DSP.
        converterData.jorbisData.block.init (converterData.jorbisData.dspState);

        converterData.pcmData.baos = new ByteArrayOutputStream ();

        /*
         * We create the PCM variables. The index is an array with the same
         * length as the number of audio channels.
         */
        converterData.pcmData.pcmInfo = new float [1] [] [];
        converterData.pcmData.pcmIndex = new int [converterData.jorbisData.info.channels];
    }

    /**
     * This method reads the entire stream body. Whenever it extracts a packet,
     * it will decode it by calling <code>decodeCurrentPacket()</code>.
     *
     * @throws JorbisReadException
     */
    private void readBody (final ConverterData converterData, final InputStream oggInputStream) throws JorbisReadException {

        /*
         * Variable used in loops below, like in readHeader(). While we need
         * more data, we will continue to read from the oggInputStream.
         */
        boolean readingBody = true;

        while (readingBody) {
            switch (converterData.joggData.syncState.pageout (converterData.joggData.page)) {
                // If we need more data, we break to get it.
                case PARTIAL_READ:
                    break;
                    // If we have successfully checked out a page, we continue.
                case PAGE_READ: 
                    readingBody = this.readBodyPacketAndDecideIfStillReading (converterData);
                    break;
                case ERROR_WHILE_READING:
                default:
                    throw new JorbisReadException ("There is a hole in the body.");
            }

            // If we need more data
            if (readingBody) {
                readingBody = !this.updatePcmDataBodyAndTellIfBodyRead (converterData, oggInputStream);
            }
        }
    }

    private boolean readBodyPacketAndDecideIfStillReading (final ConverterData converterData) throws JorbisReadException {
        boolean readingBody = true;
        // Give the page to the StreamState object.
        converterData.joggData.streamState.pagein (converterData.joggData.page);

        // If granulepos () returns "0", we don't need more data.
        if (converterData.joggData.page.granulepos () == 0) {
            readingBody = false;
        } else {
            this.processPackets (converterData);
        }

        /*
         * If the page is the end-of-stream, we don't need more data.
         */
        if (converterData.joggData.page.eos () != 0) {
            readingBody = false;
        }
        return readingBody;
    }
    
    private boolean updatePcmDataBodyAndTellIfBodyRead (final ConverterData converterData, final InputStream oggInputStream) throws JorbisReadException {
        // We get the new index and an updated buffer.
        converterData.pcmData.index = converterData.joggData.syncState.buffer (JorbisCleanConverter.BUFFER_SIZE);
        converterData.pcmData.buffer = converterData.joggData.syncState.data;

        // Read from the oggInputStream.
        try {
            converterData.pcmData.count = oggInputStream.read (converterData.pcmData.buffer, converterData.pcmData.index, JorbisCleanConverter.BUFFER_SIZE);
        } catch (final IOException e) {
            throw new JorbisReadException ("Error while reading ogg stream", e);
        }

        // We let SyncState know how many bytes we read.
        converterData.joggData.syncState.wrote (converterData.pcmData.count);

        // Check if there's no more data in the stream.
        return converterData.pcmData.count == 0;
    }

    private void processPackets (final ConverterData converterData) throws JorbisReadException {
        // Here is where we process the packets.
        boolean readingPacket = true;
        while (readingPacket) {
            switch (converterData.joggData.streamState.packetout (converterData.joggData.packet)) {
                // If we need more data, we break to get it.
                case PARTIAL_READ:
                    readingPacket = false;
                    break;
                    // If we have the data we need, we decode the packet.
                case PAGE_READ:
                    this.decodeCurrentPacket (converterData);
                    break;
                case ERROR_WHILE_READING:
                default:
                    throw new JorbisReadException ("There is a hole in a body packet.");
            }
        }
    }

    /**
     * Decodes the current packet and sends it to the audio output line.
     */
    private void decodeCurrentPacket (final ConverterData converterData) {
        int samples;

        // Check that the packet is a audio data packet etc.
        if (converterData.jorbisData.block.synthesis (converterData.joggData.packet) == 0) {
            // Give the block to the DspState object.
            converterData.jorbisData.dspState.synthesis_blockin (converterData.jorbisData.block);
        }

        // We need to know how many samples to process.
        int range;

        /*
         * Get the PCM information and count the samples. And while these
         * samples are more than zero...
         */
        while ((samples = converterData.jorbisData.dspState.synthesis_pcmout (converterData.pcmData.pcmInfo, converterData.pcmData.pcmIndex)) > 0) {
            // We need to know for how many samples we are going to process.
            if (samples < converterData.pcmData.convertedBufferSize) {
                range = samples;
            } else {
                range = converterData.pcmData.convertedBufferSize;
            }

            // For each channel...
            for (int i = 0 ; i < converterData.jorbisData.info.channels ; i++) {
                int sampleIndex = i * SAMPLE_SIZE;

                // For every sample in our range...
                for (int j = 0 ; j < range ; j++) {
                    /*
                     * Get the PCM value for the channel at the correct
                     * position.
                     */
                    final int value = this.surroundValueInReasonableRange ((int) (converterData.pcmData.pcmInfo [0] [i] [converterData.pcmData.pcmIndex [i] + j] * Short.MAX_VALUE));

                    /*
                     * Take our value and split it into two, one with the last
                     * byte and one with the first byte.
                     */
                    converterData.pcmData.convertedBuffer [sampleIndex] = (byte) value;
                    converterData.pcmData.convertedBuffer [sampleIndex + 1] = (byte) (value >>> Byte.SIZE);

                    /*
                     * Move the sample index forward by two (since that's how
                     * many values we get at once) times the number of channels.
                     */
                    sampleIndex += SAMPLE_SIZE * converterData.jorbisData.info.channels;
                }
            }

            // Write the buffer to the baos.
            converterData.pcmData.baos.write (converterData.pcmData.convertedBuffer, 0, SAMPLE_SIZE * converterData.jorbisData.info.channels * range);

            // Update the DspState object.
            converterData.jorbisData.dspState.synthesis_read (range);
        }
    }

    private int surroundValueInReasonableRange (final int value) {
        int result = value;
        /*
         * We make sure our value doesn't exceed or falls below +-32767.
         */
        if (result > Short.MAX_VALUE) {
            result = Short.MAX_VALUE;
        }
        if (result < Short.MIN_VALUE) {
            result = Short.MIN_VALUE;
        }

        /*
         * It the value is less than zero, we bitwise-or it with 32768 (which is
         * 1000000000000000 = 10^15).
         */
        if (result < 0) {
            result = value | Short.MAX_VALUE + 1;
        }
        return result;
    }

    /**
     * A clean-up method, called when everything is finished. Clears the
     * JOgg/JOrbis objects and closes the <code>oggInputStream</code>.
     */
    private void cleanUp (final ConverterData converterData, final InputStream oggInputStream) {

        // Clear the necessary JOgg/JOrbis objects.
        converterData.joggData.streamState.clear ();
        converterData.jorbisData.block.clear ();
        converterData.jorbisData.dspState.clear ();
        converterData.jorbisData.info.clear ();
        converterData.joggData.syncState.clear ();

        // Closes the stream.
        try {
            if (oggInputStream != null) {
                oggInputStream.close ();
            }
        } catch (final IOException e) {
            this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
        }

    }

    @Override
    public Entry<StreamInfo, ByteArrayOutputStream> convert (final InputStream input) throws SoundTransformException {
        ConverterData converterData;
        try {
            converterData = this.run (input);
            return new ResultEntry (this.getStreamInfo (converterData), this.getOutputStream (converterData));
        } catch (final JorbisReadException jre) {
            throw new SoundTransformException (AudioFileHelperErrorCode.CUSTOM_CONVERSION_FAILED, jre, "OGG");
        }
    }
}
