package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleImmutableEntry;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

public class JorbisDirtyConverter implements Converter {
    private static final int      BUFFER_SIZE     = (int) Math.pow (2, 11);
    
    private final Packet          joggPacket      = new Packet ();
    private final Page            joggPage        = new Page ();
    private final StreamState     joggStreamState = new StreamState ();
    private final SyncState       joggSyncState   = new SyncState ();
    private final DspState        jorbisDspState  = new DspState ();
    private final Block           jorbisBlock     = new Block (this.jorbisDspState);
    private final Comment         jorbisComment   = new Comment ();
    private final Info            jorbisInfo      = new Info ();
    /*
     * We need a buffer, it's size, a count to know how many bytes we have read
     * and an index to keep track of where we are. This is standard networking
     * stuff used with read().
     */
    private byte []               buffer          = null;
    private int                   count           = 0;
    private int                   index           = 0;

    // A three-dimensional an array with PCM information.
    private float [][][]          pcmInfo;

    // The index for the PCM information.
    private int []                pcmIndex;
    private int                   convertedBufferSize;
    private byte []               convertedBuffer;
    private ByteArrayOutputStream baos;

    public ByteArrayOutputStream getOutputStream () {
        return this.baos;
    }

    public StreamInfo getStreamInfo () {
        return new StreamInfo (this.jorbisInfo.channels, this.baos == null ? 0 : (int) (this.baos.size () * 1.0 / this.jorbisInfo.channels), 2, this.jorbisInfo.rate, false, true, "Converted from OGG Vorbis.");
    }

    /**
     * This method is probably easiest understood by looking at the body.
     * However, it will - if no problems occur - call methods to initialize the
     * JOgg JOrbis libraries, read the header, initialize the sound system, read
     * the body of the stream and clean up.
     */
    public void run (final InputStream oggInputStream) {
        // Check that we got an oggInputStream.
        if (oggInputStream == null) {
            System.err.println ("We don't have an input stream and therefore cannot continue.");
            return;
        }

        // Initialize JOrbis.
        this.initializeJorbis ();

        /*
         * If we can read the header, we try to inialize the sound system. If we
         * could initialize the sound system, we try to read the body.
         */
        if (this.readHeader (oggInputStream)) {
            if (this.initializeSound ()) {
                this.readBody (oggInputStream);
            }
        }

        // Afterwards, we clean up.
        this.cleanUp (oggInputStream);
    }

    private void initializeJorbis () {

        // Initialize SyncState
        this.joggSyncState.init ();

        // Prepare the to SyncState internal buffer
        this.joggSyncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);

        /*
         * Fill the buffer with the data from SyncState's internal buffer. Note
         * how the size of this new buffer is different from bufferSize.
         */
        this.buffer = this.joggSyncState.data;

    }

    private boolean readHeader (final InputStream oggInputStream) {

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

        /*
         * While we need more data (which we do until we have read the three
         * header packets), this loop reads from the stream and has a big
         * <code>switch</code> statement which does what it's supposed to do in
         * regards to the current packet.
         */
        while (needMoreData) {
            // Read from the oggInputStream.
            try {
                this.count = oggInputStream.read (this.buffer, this.index, JorbisDirtyConverter.BUFFER_SIZE);
            } catch (final IOException exception) {
                System.err.println ("Could not read from the input stream.");
                System.err.println (exception);
            }

            // We let SyncState know how many bytes we read.
            this.joggSyncState.wrote (this.count);

            /*
             * We want to read the first three packets. For the first packet, we
             * need to initialize the StreamState object and a couple of other
             * things. For packet two and three, the procedure is the same: we
             * take out a page, and then we take out the packet.
             */
            switch (packet) {
                // The first packet.
                case 1 : {
                    // We take out a page.
                    switch (this.joggSyncState.pageout (this.joggPage)) {
                        // If there is a hole in the data, we must exit.
                        case -1 : {
                            System.err.println ("There is a hole in the first " + "packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0 : {
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
                        case 1 : {
                            // Initializes and resets StreamState.
                            this.joggStreamState.init (this.joggPage.serialno ());
                            this.joggStreamState.reset ();

                            // Initializes the Info and Comment objects.
                            this.jorbisInfo.init ();
                            this.jorbisComment.init ();

                            // Check the page (serial number and stuff).
                            if (this.joggStreamState.pagein (this.joggPage) == -1) {
                                System.err.println ("We got an error while " + "reading the first header page.");
                                return false;
                            }

                            /*
                             * Try to extract a packet. All other return values
                             * than "1" indicates there's something wrong.
                             */
                            if (this.joggStreamState.packetout (this.joggPacket) != 1) {
                                System.err.println ("We got an error while " + "reading the first header packet.");
                                return false;
                            }

                            /*
                             * We give the packet to the Info object, so that it
                             * can extract the Comment-related information,
                             * among other things. If this fails, it's not
                             * Vorbis data.
                             */
                            if (this.jorbisInfo.synthesis_headerin (this.jorbisComment, this.joggPacket) < 0) {
                                System.err.println ("We got an error while " + "interpreting the first packet. " + "Apparantly, it's not Vorbis data.");
                                return false;
                            }

                            // We're done here, let's increment "packet".
                            packet++;
                            break;
                        }
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
                case 2 :
                case 3 : {
                    // Try to get a new page again.
                    switch (this.joggSyncState.pageout (this.joggPage)) {
                        // If there is a hole in the data, we must exit.
                        case -1 : {
                            System.err.println ("There is a hole in the second " + "or third packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0 : {
                            break;
                        }

                        /*
                         * Here is where we take the page, extract a packet and
                         * and (if everything goes well) give the information to
                         * the Info and Comment objects like we did above.
                         */
                        case 1 : {
                            // Share the page with the StreamState object.
                            this.joggStreamState.pagein (this.joggPage);

                            /*
                             * Just like the switch(...packetout...) lines
                             * above.
                             */
                            switch (this.joggStreamState.packetout (this.joggPacket)) {
                                // If there is a hole in the data, we must exit.
                                case -1 : {
                                    System.err.println ("There is a hole in the first" + "packet data.");
                                    return false;
                                }

                                // If we need more data, we break to get it.
                                case 0 : {
                                    break;
                                }

                                // We got a packet, let's process it.
                                case 1 : {
                                    /*
                                     * Like above, we give the packet to the
                                     * Info and Comment objects.
                                     */
                                    this.jorbisInfo.synthesis_headerin (this.jorbisComment, this.joggPacket);

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
            this.index = this.joggSyncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);
            this.buffer = this.joggSyncState.data;

            /*
             * If we need more data but can't get it, the stream doesn't contain
             * enough information.
             */
            if (this.count == 0 && needMoreData) {
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
    private boolean initializeSound () {

        // This buffer is used by the decoding method.
        this.convertedBufferSize = JorbisDirtyConverter.BUFFER_SIZE * 2;
        this.convertedBuffer = new byte [this.convertedBufferSize];

        // Initializes the DSP synthesis.
        this.jorbisDspState.synthesis_init (this.jorbisInfo);

        // Make the Block object aware of the DSP.
        this.jorbisBlock.init (this.jorbisDspState);

        this.baos = new ByteArrayOutputStream ();

        /*
         * We create the PCM variables. The index is an array with the same
         * length as the number of audio channels.
         */
        this.pcmInfo = new float [1] [] [];
        this.pcmIndex = new int [this.jorbisInfo.channels];
        return true;
    }

    /**
     * This method reads the entire stream body. Whenever it extracts a packet,
     * it will decode it by calling <code>decodeCurrentPacket()</code>.
     */
    private void readBody (final InputStream oggInputStream) {

        /*
         * Variable used in loops below, like in readHeader(). While we need
         * more data, we will continue to read from the oggInputStream.
         */
        boolean needMoreData = true;

        while (needMoreData) {
            switch (this.joggSyncState.pageout (this.joggPage)) {

                // If we need more data, we break to get it.
                case 0 : {
                    break;
                }

                // If we have successfully checked out a page, we continue.
                case 1 : {
                    // Give the page to the StreamState object.
                    this.joggStreamState.pagein (this.joggPage);

                    // If granulepos() returns "0", we don't need more data.
                    if (this.joggPage.granulepos () == 0) {
                        needMoreData = false;
                        break;
                    }

                    // Here is where we process the packets.
                    processPackets : while (true) {
                        switch (this.joggStreamState.packetout (this.joggPacket)) {

                            // If we need more data, we break to get it.
                            case 0 : {
                                break processPackets;
                            }

                            /*
                             * If we have the data we need, we decode the
                             * packet.
                             */
                            case 1 : {
                                this.decodeCurrentPacket ();
                            }
                        }
                    }

                    /*
                     * If the page is the end-of-stream, we don't need more
                     * data.
                     */
                    if (this.joggPage.eos () != 0) {
                        needMoreData = false;
                    }
                }
            }

            // If we need more data
            if (needMoreData) {
                // We get the new index and an updated buffer.
                this.index = this.joggSyncState.buffer (JorbisDirtyConverter.BUFFER_SIZE);
                this.buffer = this.joggSyncState.data;

                // Read from the oggInputStream.
                try {
                    this.count = oggInputStream.read (this.buffer, this.index, JorbisDirtyConverter.BUFFER_SIZE);
                } catch (final Exception e) {
                    System.err.println (e);
                    return;
                }

                // We let SyncState know how many bytes we read.
                this.joggSyncState.wrote (this.count);

                // There's no more data in the stream.
                if (this.count == 0) {
                    needMoreData = false;
                }
            }
        }
    }

    /**
     * Decodes the current packet and sends it to the audio output line.
     */
    private void decodeCurrentPacket () {
        int samples;

        // Check that the packet is a audio data packet etc.
        if (this.jorbisBlock.synthesis (this.joggPacket) == 0) {
            // Give the block to the DspState object.
            this.jorbisDspState.synthesis_blockin (this.jorbisBlock);
        }

        // We need to know how many samples to process.
        int range;

        /*
         * Get the PCM information and count the samples. And while these
         * samples are more than zero...
         */
        while ((samples = this.jorbisDspState.synthesis_pcmout (this.pcmInfo, this.pcmIndex)) > 0) {
            // We need to know for how many samples we are going to process.
            if (samples < this.convertedBufferSize) {
                range = samples;
            } else {
                range = this.convertedBufferSize;
            }

            // For each channel...
            for (int i = 0 ; i < this.jorbisInfo.channels ; i++) {
                int sampleIndex = i * 2;

                // For every sample in our range...
                for (int j = 0 ; j < range ; j++) {
                    /*
                     * Get the PCM value for the channel at the correct
                     * position.
                     */
                    int value = (int) (this.pcmInfo [0] [i] [this.pcmIndex [i] + j] * Short.MAX_VALUE);

                    /*
                     * We make sure our value doesn't exceed or falls below
                     * +-32767.
                     */
                    if (value > Short.MAX_VALUE) {
                        value = Short.MAX_VALUE;
                    }
                    if (value < Short.MIN_VALUE) {
                        value = Short.MIN_VALUE;
                    }

                    /*
                     * It the value is less than zero, we bitwise-or it with
                     * 32768 (which is 1000000000000000 = 10^15).
                     */
                    if (value < 0) {
                        value = value | (Short.MAX_VALUE + 1);
                    }

                    /*
                     * Take our value and split it into two, one with the last
                     * byte and one with the first byte.
                     */
                    this.convertedBuffer [sampleIndex] = (byte) value;
                    this.convertedBuffer [sampleIndex + 1] = (byte) (value >>> 8);

                    /*
                     * Move the sample index forward by two (since that's how
                     * many values we get at once) times the number of channels.
                     */
                    sampleIndex += 2 * this.jorbisInfo.channels;
                }
            }

            // Write the buffer to the baos.
            this.baos.write (this.convertedBuffer, 0, 2 * this.jorbisInfo.channels * range);

            // Update the DspState object.
            this.jorbisDspState.synthesis_read (range);
        }
    }

    /**
     * A clean-up method, called when everything is finished. Clears the
     * JOgg/JOrbis objects and closes the <code>oggInputStream</code>.
     */
    private void cleanUp (final InputStream oggInputStream) {

        // Clear the necessary JOgg/JOrbis objects.
        this.joggStreamState.clear ();
        this.jorbisBlock.clear ();
        this.jorbisDspState.clear ();
        this.jorbisInfo.clear ();
        this.joggSyncState.clear ();

        // Closes the stream.
        try {
            if (oggInputStream != null) {
                oggInputStream.close ();
            }
        } catch (final IOException e) {
        }

    }

    @Override
    public SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> convert (InputStream input) throws SoundTransformException {
        this.run (input);
        return new SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> (this.getStreamInfo (), this.getOutputStream ());
    }
}
