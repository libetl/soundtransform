package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;

@Processor
final class ByteArrayFrameProcessor extends AbstractLogAware<ByteArrayFrameProcessor> implements FrameProcessor<AbstractLogAware<ByteArrayFrameProcessor>> {

    private static final int   NB_BYTE_VALUES           = 1 << Byte.SIZE;
    private static final int   MAX_BYTE_VALUE           = ByteArrayFrameProcessor.NB_BYTE_VALUES - 1;
    private static final float PERCENT                  = 100.0f;
    private static final int   HALF                     = 2;
    private static final int   TWICE                    = 2;
    private static final int   EACH_X_PERCENT           = 5;
    private static final int   EACH_X_PERCENT_MINUS_ONE = ByteArrayFrameProcessor.EACH_X_PERCENT - 1;

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
     * FrameProcessor#byteArrayToFrame(byte[],
     * org.toilelibre.libe.soundtransform.model.sound.Sound[], int, boolean,
     * boolean, long)
     */
    private void byteArrayToFrame (final byte [] frame, final Channel [] sound, final int position, final boolean bigEndian, final boolean pcmSigned, final long neutral) {
        final long [] value = new long [sound.length];
        final int destination = bigEndian ? 0 : frame.length - 1;
        for (int j = 0 ; j < frame.length ; j++) {
            final int cursor = bigEndian ? frame.length - j - 1 : j;
            final int fromIndex = cursor < destination ? cursor : destination;
            final int toIndex = cursor < destination ? destination : cursor;
            final int currentChannel = bigEndian ? sound.length - 1 - j / (frame.length / sound.length) : j / (frame.length / sound.length);
            final int numByte = j % (frame.length / sound.length);
            if (fromIndex <= toIndex) {
                value [currentChannel] += frame [cursor] + (pcmSigned ? -Byte.MIN_VALUE : 0) << Byte.SIZE * numByte;
            }

        }

        for (int i = 0 ; i < sound.length ; i++) {
            sound [i].setSampleAt (position, value [i] - neutral);
        }
    }

    private void closeInputStream (final InputStream ais) throws SoundTransformException {
        try {
            ais.close ();
        } catch (final IOException e) {
            throw new SoundTransformException (FrameProcessorErrorCode.COULD_NOT_CLOSE_STREAM, e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
     * FrameProcessor
     * #framesToByteArray(org.toilelibre.libe.soundtransform.model.
     * sound.Sound[], int, boolean, boolean)
     */
    @Override
    public byte [] framesToByteArray (final Channel [] channels, final StreamInfo streamInfo) {
        final boolean pcmSigned = streamInfo.isPcmSigned ();
        final boolean bigEndian = streamInfo.isBigEndian ();
        final int sampleSize = streamInfo.getSampleSize ();

        final int length = channels.length * sampleSize * channels [0].getSamplesLength ();
        final byte [] data = new byte [length];

        double value = 0;
        int rightShift = 0;
        int byteValueSigned;
        final long neutral = pcmSigned ? this.getNeutral (sampleSize) : 0;
        for (int i = 0 ; i < data.length ; i++) {
            final int numByte = i % sampleSize;
            final int currentChannel = i / sampleSize % channels.length;
            final int currentFrame = i / (sampleSize * channels.length);
            if (numByte == 0 && channels [currentChannel].getSamplesLength () > currentFrame) {
                value = channels [currentChannel].getSampleAt (currentFrame) + neutral;
                rightShift = 0;
            }
            final int byteValueWithoutSign = (int) value >> rightShift * Byte.SIZE & ByteArrayFrameProcessor.MAX_BYTE_VALUE;

            byteValueSigned = byteValueWithoutSign + (pcmSigned ? Byte.MIN_VALUE : 0);

            data [i + (bigEndian ? sampleSize - ByteArrayFrameProcessor.TWICE * numByte - 1 : 0)] = (byte) byteValueSigned;
            rightShift++;
        }
        return data;
    }

    @Override
    public Channel [] fromInputStream (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (FrameProcessorEventCode.SOUND_INIT));
        final Channel [] ret = this.initSound (ais, isInfo);
        this.log (new LogEvent (FrameProcessorEventCode.READ_START));
        this.writeSound (ais, isInfo, ret);
        this.closeInputStream (ais);
        this.log (new LogEvent (FrameProcessorEventCode.READ_END));
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
     * FrameProcessor#getNeutral(int)
     */
    private long getNeutral (final int sampleSize) {
        long neutral = 0;
        for (int i = 1 ; i <= sampleSize ; i++) {
            neutral += Math.pow (ByteArrayFrameProcessor.MAX_BYTE_VALUE, i) / ByteArrayFrameProcessor.HALF;
        }
        return neutral;
    }

    private int getPercent (final int position, final long length) {
        return (int) (position * ByteArrayFrameProcessor.PERCENT / length);
    }

    private Channel [] initSound (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        final Channel [] ret = new Channel [isInfo.getChannels ()];
        for (int channel = 0 ; channel < isInfo.getChannels () ; channel++) {
            ret [channel] = new Channel (new long [this.findFrameLength (ais, isInfo)], isInfo, channel);
        }
        return ret;
    }

    private int findFrameLength (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        try {
            return isInfo.getFrameLength () > 0 ? (int) isInfo.getFrameLength () : ais.available () / isInfo.getSampleSize ();
        } catch (final IOException e) {
            throw new SoundTransformException (FrameProcessorErrorCode.COULD_NOT_FIND_LENGTH, e);
        }
    }

    private void writeSound (final InputStream ais, final StreamInfo isInfo, final Channel [] result) throws SoundTransformException {
        final long neutral = this.getNeutral (isInfo.getSampleSize ());
        final int frameLength = this.findFrameLength (ais, isInfo);
        for (int position = 0 ; position < frameLength ; position++) {
            final byte [] frame = new byte [isInfo.getSampleSize () * isInfo.getChannels ()];
            try {
                this.readOneFrame (ais, frame);
            } catch (final IOException e) {
                throw new SoundTransformException (FrameProcessorErrorCode.COULD_NOT_READ_STREAM, e);
            }
            final int currentPercent = this.getPercent (position, isInfo.getFrameLength ());
            final int lastPercent = this.getPercent (position - 1, isInfo.getFrameLength ());
            if (currentPercent % ByteArrayFrameProcessor.EACH_X_PERCENT == 0 && lastPercent % ByteArrayFrameProcessor.EACH_X_PERCENT == ByteArrayFrameProcessor.EACH_X_PERCENT_MINUS_ONE) {
                this.log (new LogEvent (FrameProcessorEventCode.BYTEARRAY_TO_FRAME_CONVERSION, position, isInfo.getFrameLength (), currentPercent));
            }
            this.byteArrayToFrame (frame, result, position, isInfo.isBigEndian (), isInfo.isPcmSigned (), neutral);
        }
    }

    private void readOneFrame (final InputStream ais, final byte [] frame) throws IOException {
        final int size = ais.read (frame);
        if (size == -1) {
            this.log (new LogEvent (FrameProcessorEventCode.END_OF_STREAM));
        }

    }

}
