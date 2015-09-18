package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

class JorbisDirtyConverter implements Converter {

    static class ConverterData {
        JoggData   joggData   = new JoggData ();
        JorbisData jorbisData = new JorbisData ();
    }

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

    private static final int BUFFER_SIZE = (int) Math.pow (2, 11);

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

    public ByteArrayOutputStream getOutputStream (PcmData pcmData) {
        return pcmData.baos;
    }

    public StreamInfo getStreamInfo (JorbisData jorbisData, PcmData pcmData) {
        return new StreamInfo (jorbisData.info.channels, pcmData.baos == null ? 0 : (int) (pcmData.baos.size () * 1.0 / jorbisData.info.channels), 2, jorbisData.info.rate, false, true, "Converted from OGG Vorbis.");
    }

    /**
     * This method is probably easiest understood by looking at the body.
     * However, it will - if no problems occur - call methods to initialize the
     * JOgg JOrbis libraries, read the header, initialize the sound system, read
     * the body of the stream and clean up.
     */
    public AbstractMap.SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> convert (final InputStream oggInputStream) {
        // Check that we got an oggInputStream.
        if (oggInputStream == null) {
            System.err.println ("We don't have an input stream and therefore cannot continue.");
            return null;
        }
        ConverterData converterData = new ConverterData ();
        PcmData pcmData = new PcmData ();

        // Initialize JOrbis.
        this.initializeJoggData (converterData.joggData, pcmData);

        /*
         * If we can read the header, we try to inialize the sound system. If we
         * could initialize the sound system, we try to read the body.
         */
        if (this.readHeader (converterData, pcmData, oggInputStream)) {
            this.initializeSound (converterData.jorbisData, pcmData);
            this.readBody (converterData, pcmData, oggInputStream);
        }

        // Afterwards, we clean up.
        this.cleanUp (converterData, oggInputStream);
        return new AbstractMap.SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> (this.getStreamInfo (converterData.jorbisData, pcmData), pcmData.baos);
    }

    private void initializeJoggData (JoggData joggData, PcmData pcmData) {

        // Initialize SyncState
        joggData.syncState.init ();

        // Prepare the to SyncState internal buffer
        joggData.syncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);

        /*
         * Fill the buffer with the data from SyncState's internal buffer. Note
         * how the size of this new buffer is different from bufferSize.
         */
        pcmData.buffer = joggData.syncState.data;

    }

    private boolean readHeader (ConverterData converterData, PcmData pcmData, final InputStream oggInputStream) {

        /*
         * Variable used in loops below. While we need more data, we will
         * continue to read from the oggInputStream.
         */
        boolean needMoreData = true;

        /*
         * We will read the first three packets of the header. We start off by
         * defining packet = 1 and increment that value whenever we have
         * successfully read another packet.
         */
        int packet = 1;

        JoggData joggData = converterData.joggData;
        JorbisData jorbisData = converterData.jorbisData;
        /*
         * While we need more data (which we do until we have read the three
         * header packets), this loop reads from the stream and has a big
         * <code>switch</code> statement which does what it's supposed to do in
         * regards to the current packet.
         */
        while (needMoreData) {
            // Read from the oggInputStream.
            try {
                pcmData.count = oggInputStream.read (pcmData.buffer, pcmData.index, JorbisDirtyConverter.BUFFER_SIZE);
            } catch (final IOException exception) {
                System.err.println ("Could not read from the input stream.");
                System.err.println (exception);
            }

            // We let SyncState know how many bytes we read.
            joggData.syncState.wrote (pcmData.count);

            /*
             * We want to read the first three packets. For the first packet, we
             * need to initialize the StreamState object and a couple of other
             * things. For packet two and three, the procedure is the same: we
             * take out a page, and then we take out the packet.
             */
            switch (packet) {
            // The first packet.
                case 1: {
                    // We take out a page.
                    switch (joggData.syncState.pageout (joggData.page)) {
                    // If there is a hole in the data, we must exit.
                        case -1: {
                            System.err.println ("There is a hole in the first " + "packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0: {
                            break;
                        }

                        /*
                         * We got where we wanted. We have successfully read the
                         * first packet, and we will now initialize and reset
                         * StreamState, and initialize the Info and Comment
                         * objects. Afterwards we will check that the page
                         * doesn't contain any errors, that the packet doesn't
                         * contain any errors and that it's Vorbis data.
                         */
                        case 1: {
                            // Initializes and resets StreamState.
                            joggData.streamState.init (joggData.page.serialno ());
                            joggData.streamState.reset ();

                            // Initializes the Info and Comment objects.
                            jorbisData.info.init ();
                            jorbisData.comment.init ();

                            // Check the page (serial number and stuff).
                            if (joggData.streamState.pagein (joggData.page) == -1) {
                                System.err.println ("We got an error while " + "reading the first header page.");
                                return false;
                            }

                            /*
                             * Try to extract a packet. All other return values
                             * than "1" indicates there's something wrong.
                             */
                            if (joggData.streamState.packetout (joggData.packet) != 1) {
                                System.err.println ("We got an error while " + "reading the first header packet.");
                                return false;
                            }

                            /*
                             * We give the packet to the Info object, so that it
                             * can extract the Comment-related information,
                             * among other things. If this fails, it's not
                             * Vorbis data.
                             */
                            if (jorbisData.info.synthesis_headerin (jorbisData.comment, joggData.packet) < 0) {
                                System.err.println ("We got an error while " + "interpreting the first packet. " + "Apparantly, it's not Vorbis data.");
                                return false;
                            }

                            // We're done here, let's increment "packet".
                            packet++;
                            break;
                        }
                        default:
                            break;
                    }

                    /*
                     * Note how we are NOT breaking here if we have proceeded to
                     * the second packet. We don't want to read from the input
                     * stream again if it's not necessary.
                     */
                    if (packet == 1) {
                        break;
                    }
                }

                // The code for the second and third packets follow.
                case 2:
                case 3: {
                    // Try to get a new page again.
                    switch (joggData.syncState.pageout (joggData.page)) {
                    // If there is a hole in the data, we must exit.
                        case -1: {
                            System.err.println ("There is a hole in the second " + "or third packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0: {
                            break;
                        }

                        /*
                         * Here is where we take the page, extract a packet and
                         * and (if everything goes well) give the information to
                         * the Info and Comment objects like we did above.
                         */
                        case 1: {
                            // Share the page with the StreamState object.
                            joggData.streamState.pagein (joggData.page);

                            /*
                             * Just like the switch(...packetout...) lines
                             * above.
                             */
                            switch (joggData.streamState.packetout (joggData.packet)) {
                            // If there is a hole in the data, we must exit.
                                case -1: {
                                    System.err.println ("There is a hole in the first" + "packet data.");
                                    return false;
                                }

                                // If we need more data, we break to get it.
                                case 0: {
                                    break;
                                }

                                // We got a packet, let's process it.
                                case 1: {
                                    /*
                                     * Like above, we give the packet to the
                                     * Info and Comment objects.
                                     */
                                    jorbisData.info.synthesis_headerin (jorbisData.comment, joggData.packet);

                                    // Increment packet.
                                    packet++;

                                    if (packet == 4) {
                                        /*
                                         * There is no fourth packet, so we will
                                         * just end the loop here.
                                         */
                                        needMoreData = false;
                                    }

                                    break;
                                }
                            }

                            break;
                        }
                    }

                    break;
                }
            }

            // We get the new index and an updated buffer.
            pcmData.index = joggData.syncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);
            pcmData.buffer = joggData.syncState.data;

            /*
             * If we need more data but can't get it, the stream doesn't contain
             * enough information.
             */
            if (pcmData.count == 0 && needMoreData) {
                System.err.println ("Not enough header data was supplied.");
                return false;
            }
        }

        return true;
    }

    /**
     * This method starts the sound system. It starts with initializing the
     * <code>DspState</code> object, after which it sets up the
     * <code>Block</code> object. Last but not least, it opens a line to the
     * source data line.
     *
     * @return true if the sound system was successfully started, false
     *         otherwise
     */
    private void initializeSound (JorbisData jorbisData, PcmData pcmData) {

        // This buffer is used by the decoding method.
        pcmData.convertedBufferSize = JorbisDirtyConverter.BUFFER_SIZE * 2;
        pcmData.convertedBuffer = new byte [pcmData.convertedBufferSize];

        // Initializes the DSP synthesis.
        jorbisData.dspState.synthesis_init (jorbisData.info);

        // Make the Block object aware of the DSP.
        jorbisData.block.init (jorbisData.dspState);

        pcmData.baos = new ByteArrayOutputStream ();

        /*
         * We create the PCM variables. The index is an array with the same
         * length as the number of audio channels.
         */
        pcmData.pcmInfo = new float [1] [] [];
        pcmData.pcmIndex = new int [jorbisData.info.channels];
    }

    /**
     * This method reads the entire stream body. Whenever it extracts a packet,
     * it will decode it by calling <code>decodeCurrentPacket()</code>.
     */
    private void readBody (ConverterData converterData, PcmData pcmData, final InputStream oggInputStream) {

        /*
         * Variable used in loops below, like in readHeader(). While we need
         * more data, we will continue to read from the oggInputStream.
         */
        boolean needMoreData = true;

        JoggData joggData = converterData.joggData;
        while (needMoreData) {
            switch (joggData.syncState.pageout (joggData.page)) {

            // If we need more data, we break to get it.
                case 0: {
                    break;
                }

                // If we have successfully checked out a page, we continue.
                case 1: {
                    // Give the page to the StreamState object.
                    joggData.streamState.pagein (joggData.page);

                    // If granulepos() returns "0", we don't need more data.
                    if (joggData.page.granulepos () == 0) {
                        needMoreData = false;
                        break;
                    }

                    // Here is where we process the packets.
                    processPackets : while (true) {
                        switch (joggData.streamState.packetout (joggData.packet)) {

                        // If we need more data, we break to get it.
                            case 0: {
                                break processPackets;
                            }

                            /*
                             * If we have the data we need, we decode the
                             * packet.
                             */
                            case 1: {
                                this.decodeCurrentPacket (converterData, pcmData);
                            }
                        }
                    }

                    /*
                     * If the page is the end-of-stream, we don't need more
                     * data.
                     */
                    if (joggData.page.eos () != 0) {
                        needMoreData = false;
                    }
                }
            }

            // If we need more data
            if (needMoreData) {
                // We get the new index and an updated buffer.
                pcmData.index = joggData.syncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);
                pcmData.buffer = joggData.syncState.data;

                // Read from the oggInputStream.
                try {
                    pcmData.count = oggInputStream.read (pcmData.buffer, pcmData.index, JorbisDirtyConverter.BUFFER_SIZE);
                } catch (final Exception e) {
                    System.err.println (e);
                    return;
                }

                // We let SyncState know how many bytes we read.
                joggData.syncState.wrote (pcmData.count);

                // There's no more data in the stream.
                if (pcmData.count == 0) {
                    needMoreData = false;
                }
            }
        }
    }

    /**
     * Decodes the current packet and sends it to the audio output line.
     */
    private void decodeCurrentPacket (ConverterData converterData, PcmData pcmData) {

        JoggData joggData = converterData.joggData;
        JorbisData jorbisData = converterData.jorbisData;

        // Check that the packet is a audio data packet etc.
        if (jorbisData.block.synthesis (joggData.packet) == 0) {
            // Give the block to the DspState object.
            jorbisData.dspState.synthesis_blockin (jorbisData.block);
        }

        this.getPCMInformationAndCountSamples (jorbisData, pcmData);
    }

    private void getPCMInformationAndCountSamples (JorbisData jorbisData, PcmData pcmData) {
        int samples;
        // We need to know how many samples to process.
        int range;

        /*
         * Get the PCM information and count the samples. And while these
         * samples are more than zero...
         */
        while ((samples = jorbisData.dspState.synthesis_pcmout (pcmData.pcmInfo, pcmData.pcmIndex)) > 0) {
            // We need to know for how many samples we are going to process.
            if (samples < pcmData.convertedBufferSize) {
                range = samples;
            } else {
                range = pcmData.convertedBufferSize;
            }

            // For each channel...
            for (int i = 0 ; i < jorbisData.info.channels ; i++) {
                int sampleIndex = i * 2;

                // For every sample in our range...
                for (int j = 0 ; j < range ; j++) {
                    this.getSampleValue (jorbisData, pcmData, i, sampleIndex, j);
                }
            }

            // Write the buffer to the baos.
            pcmData.baos.write (pcmData.convertedBuffer, 0, 2 * jorbisData.info.channels * range);

            // Update the DspState object.
            jorbisData.dspState.synthesis_read (range);
        }
    }

    private void getSampleValue (JorbisData jorbisData, PcmData pcmData, int i, int sampleIndex, int j) {
        /*
         * Get the PCM value for the channel at the correct position.
         */
        int value = (int) (pcmData.pcmInfo [0] [i] [pcmData.pcmIndex [i] + j] * Short.MAX_VALUE);

        /*
         * We make sure our value doesn't exceed or falls below +-32767.
         */
        if (value > Short.MAX_VALUE) {
            value = Short.MAX_VALUE;
        }
        if (value < Short.MIN_VALUE) {
            value = Short.MIN_VALUE;
        }

        /*
         * It the value is less than zero, we bitwise-or it with 32768 (which is
         * 1000000000000000 = 10^15).
         */
        if (value < 0) {
            value = value | (Short.MAX_VALUE + 1);
        }

        /*
         * Take our value and split it into two, one with the last byte and one
         * with the first byte.
         */
        pcmData.convertedBuffer [sampleIndex] = (byte) value;
        pcmData.convertedBuffer [sampleIndex + 1] = (byte) (value >>> Byte.SIZE);

        /*
         * Move the sample index forward by two (since that's how many values we
         * get at once) times the number of channels.
         * 
         * should add 2 * jorbisData.info.channels to sampleIndex but we will
         * not use it after
         */
    }

    /**
     * A clean-up method, called when everything is finished. Clears the
     * JOgg/JOrbis objects and closes the <code>oggInputStream</code>.
     */
    private void cleanUp (final ConverterData converterData, final InputStream oggInputStream) {

        JoggData joggData = converterData.joggData;
        JorbisData jorbisData = converterData.jorbisData;
        // Clear the necessary JOgg/JOrbis objects.
        joggData.streamState.clear ();
        jorbisData.block.clear ();
        jorbisData.dspState.clear ();
        jorbisData.info.clear ();
        joggData.syncState.clear ();

        // Closes the stream.
        try {
            if (oggInputStream != null) {
                oggInputStream.close ();
            }
        } catch (final IOException e) {
        }

    }
}
