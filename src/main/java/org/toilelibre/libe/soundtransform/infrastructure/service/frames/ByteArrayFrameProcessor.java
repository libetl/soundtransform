package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService.TransformInputStreamServiceErrorCode;

public class ByteArrayFrameProcessor implements FrameProcessor {

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
     * FrameProcessor#byteArrayToFrame(byte[],
     * org.toilelibre.libe.soundtransform.model.sound.Sound[], int, boolean,
     * boolean, long)
     */
    @Override
    public void byteArrayToFrame (final byte [] frame, final Sound [] sound,
            final int position, final boolean bigEndian,
            final boolean pcmSigned, final long neutral) {
        final long [] value = new long [sound.length];
        final int destination = bigEndian ? 0 : frame.length - 1;
        for (int j = 0 ; j < frame.length ; j++) {
            final int cursor = bigEndian ? frame.length - j - 1 : j;
            final int fromIndex = cursor < destination ? cursor : destination;
            final int toIndex = cursor < destination ? destination : cursor;
            final int currentChannel = !bigEndian ? j
                    / (frame.length / sound.length) : sound.length - 1 - j
                    / (frame.length / sound.length);
            final int numByte = j % (frame.length / sound.length);
            if (fromIndex <= toIndex) {
                value [currentChannel] += (frame [cursor] + (pcmSigned ? -Byte.MIN_VALUE
                        : 0))
                        * Math.pow (256, numByte);
            }

        }

        for (int i = 0 ; i < sound.length ; i++) {
            sound [i].getSamples () [position] = value [i] - neutral;
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
    public byte [] framesToByteArray (final Sound [] channels,
            final int sampleSize, final boolean bigEndian,
            final boolean pcmSigned) {
        final int length = channels.length * sampleSize
                * channels [0].getSamples ().length;
        final byte [] data = new byte [length];

        double value = 0;
        double dividedValue = 0;
        byte byteValueSigned = 0;
        final long neutral = pcmSigned ? this.getNeutral (sampleSize) : 0;
        for (int i = 0 ; i < data.length ; i++) {
            final int numByte = i % sampleSize;
            final int currentChannel = i / sampleSize % channels.length;
            final int currentFrame = i / (sampleSize * channels.length);
            if (numByte == 0
                    && channels [currentChannel].getSamples ().length > currentFrame) {
                value = channels [currentChannel].getSamples () [currentFrame]
                        + neutral;
            }
            dividedValue = value / 256;
            byteValueSigned = (byte) (value + (pcmSigned ? Byte.MIN_VALUE : 0));

            data [i + (!bigEndian ? 0 : sampleSize - 2 * numByte - 1)] = byteValueSigned;
            value = dividedValue;
        }
        return data;
    }

    @Override
    public Sound [] fromInputStream (final InputStream ais,
            final InputStreamInfo isInfo) throws SoundTransformException {
        final int channels = isInfo.getChannels ();
        final int sampleSize = isInfo.getSampleSize ();
        final int frameLength = (int) isInfo.getFrameLength ();
        final Sound [] ret = new Sound [channels];
        final long neutral = isInfo.isPcmSigned () ? this
                .getNeutral (sampleSize) : 0;
        for (int channel = 0 ; channel < channels ; channel++) {
            ret [channel] = new Sound (new long [frameLength], sampleSize,
                    (int) isInfo.getSampleRate (), channel);
        }
        for (int position = 0 ; position < frameLength ; position++) {
            final byte [] frame = new byte [sampleSize * channels];
            try {
                ais.read (frame);
            } catch (final IOException e) {
                throw new SoundTransformException (
                        TransformInputStreamServiceErrorCode.COULD_NOT_READ_STREAM,
                        e);
            }
            this.byteArrayToFrame (frame, ret, position, isInfo.isBigEndian (),
                    isInfo.isPcmSigned (), neutral);
        }
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
            neutral += Math.pow (Byte.MAX_VALUE - Byte.MIN_VALUE, i) / 2;
        }
        return neutral;
    }

}
