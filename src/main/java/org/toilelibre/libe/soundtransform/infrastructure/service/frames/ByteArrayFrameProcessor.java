package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService.TransformInputStreamServiceErrorCode;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

public class ByteArrayFrameProcessor extends AbstractLogAware<ByteArrayFrameProcessor> implements FrameProcessor<AbstractLogAware<ByteArrayFrameProcessor>> {

    private static final int   NB_BYTE_VALUES = 1 << Byte.SIZE;
    private static final int   MAX_BYTE_VALUE = ByteArrayFrameProcessor.NB_BYTE_VALUES - 1;
    private static final float PERCENT        = 100.0f;
    private static final int   HALF           = 2;
    private static final int   TWICE          = 2;

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
     * FrameProcessor#byteArrayToFrame(byte[],
     * org.toilelibre.libe.soundtransform.model.sound.Sound[], int, boolean,
     * boolean, long)
     */
    @Override
    public void byteArrayToFrame (final byte [] frame, final Sound [] sound, final int position, final boolean bigEndian, final boolean pcmSigned, final long neutral) {
        final long [] value = new long [sound.length];
        final int destination = bigEndian ? 0 : frame.length - 1;
        for (int j = 0 ; j < frame.length ; j++) {
            final int cursor = bigEndian ? frame.length - j - 1 : j;
            final int fromIndex = cursor < destination ? cursor : destination;
            final int toIndex = cursor < destination ? destination : cursor;
            final int currentChannel = !bigEndian ? j / (frame.length / sound.length) : sound.length - 1 - (j / (frame.length / sound.length));
            final int numByte = j % (frame.length / sound.length);
            if (fromIndex <= toIndex) {
                value [currentChannel] += (frame [cursor] + (pcmSigned ? -Byte.MIN_VALUE : 0)) << (Byte.SIZE * numByte);
            }

        }

        for (int i = 0 ; i < sound.length ; i++) {
            sound [i].getSamples () [position] = value [i] - neutral;
        }
    }

    private void closeInputStream (InputStream ais) throws SoundTransformException {
        try {
            ais.close ();
        } catch (final IOException e) {
            throw new SoundTransformException (TransformInputStreamServiceErrorCode.COULD_NOT_CLOSE_STREAM, e);
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
    public byte [] framesToByteArray (final Sound [] channels, final int sampleSize, final boolean bigEndian, final boolean pcmSigned) {
        final int length = channels.length * sampleSize * channels [0].getSamples ().length;
        final byte [] data = new byte [length];

        double value = 0;
        int rightShift = 0;
        byte byteValueSigned = 0;
        final long neutral = pcmSigned ? this.getNeutral (sampleSize) : 0;
        for (int i = 0 ; i < data.length ; i++) {
            final int numByte = i % sampleSize;
            final int currentChannel = (i / sampleSize) % channels.length;
            final int currentFrame = i / (sampleSize * channels.length);
            if ((numByte == 0) && (channels [currentChannel].getSamples ().length > currentFrame)) {
                value = channels [currentChannel].getSamples () [currentFrame] + neutral;
                rightShift = 0;
            }
            byteValueSigned = (byte) ((((int) value >> (rightShift * Byte.SIZE)) & ByteArrayFrameProcessor.MAX_BYTE_VALUE) + (pcmSigned ? Byte.MIN_VALUE : 0));

            data [i + (!bigEndian ? 0 : sampleSize - (ByteArrayFrameProcessor.TWICE * numByte) - 1)] = byteValueSigned;
            rightShift++;
        }
        return data;
    }

    @Override
    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (FrameProcessorEventCode.SOUND_INIT));
        final Sound [] ret = this.initSound (isInfo);
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

    private int getPercent (int position, long length) {
        return Math.round ((position * ByteArrayFrameProcessor.PERCENT) / length);
    }

    private Sound [] initSound (final InputStreamInfo isInfo) {
        final Sound [] ret = new Sound [isInfo.getChannels ()];
        for (int channel = 0 ; channel < isInfo.getChannels () ; channel++) {
            ret [channel] = new Sound (new long [(int) isInfo.getFrameLength ()], isInfo.getSampleSize (), (int) isInfo.getSampleRate (), channel);
        }
        return ret;
    }

    private void writeSound (final InputStream ais, final InputStreamInfo isInfo, final Sound [] result) throws SoundTransformException {
        final long neutral = isInfo.isPcmSigned () ? this.getNeutral (isInfo.getSampleSize ()) : 0;
        for (int position = 0 ; position < (int) isInfo.getFrameLength () ; position++) {
            final byte [] frame = new byte [isInfo.getSampleSize () * isInfo.getChannels ()];
            try {
                int frameSize = ais.read (frame);
                this.log (new LogEvent (FrameProcessorEventCode.READ_FRAME_SIZE, frameSize));
            } catch (final IOException e) {
                throw new SoundTransformException (TransformInputStreamServiceErrorCode.COULD_NOT_READ_STREAM, e);
            }
            if (this.getPercent (position, isInfo.getFrameLength ()) != this.getPercent (position - 1, isInfo.getFrameLength ())) {
                this.log (new LogEvent (FrameProcessorEventCode.BYTEARRAY_TO_FRAME_CONVERSION, position, isInfo.getFrameLength (), this.getPercent (position, isInfo.getFrameLength ())));
            }
            this.byteArrayToFrame (frame, result, position, isInfo.isBigEndian (), isInfo.isPcmSigned (), neutral);
        }
    }

}
