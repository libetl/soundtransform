package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;

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
	public void byteArrayToFrame (byte [] frame, Sound [] sound, int position, boolean bigEndian, boolean pcmSigned, long neutral) {
		long [] value = new long [sound.length];
		int destination = (bigEndian ? 0 : frame.length - 1);
		for (int j = 0; j < frame.length; j++) {
			int cursor = (bigEndian ? frame.length - j - 1 : j);
			int fromIndex = (cursor < destination ? cursor : destination);
			int toIndex = (cursor < destination ? destination : cursor);
			int currentChannel = (!bigEndian ? j / (frame.length / sound.length) : sound.length - 1 - j / (frame.length / sound.length));
			int numByte = j % (frame.length / sound.length);
			if (fromIndex <= toIndex) {
				// double oldValue = value [currentChannel];
				value [currentChannel] += (frame [cursor] + (pcmSigned ? -Byte.MIN_VALUE : 0)) * Math.pow (256, numByte);
				// this.log(new LogEvent (LogLevel.PARANOIAC, "Building Sample #" + position + ", channel " + currentChannel + ", numByte : " + numByte + ", value : " + String.format("%.0f", oldValue) + " + ((" + (frame [cursor] + " +  " + (pcmSigned ? -Byte.MIN_VALUE : 0)) + ") * 256^" + numByte + ") = " + String.format("%.0f", value [currentChannel])));
			}

		}
		// this.log(new LogEvent (LogLevel.PARANOIAC, "Sample #" + position + " values : " + Arrays.toString (value)));

		for (int i = 0; i < sound.length; i++) {
			sound [i].getSamples () [position] = value [i] - neutral;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.toilelibre.libe.soundtransform.infrastructure.service.frames.
	 * FrameProcessor#getNeutral(int)
	 */
	@Override
	public long getNeutral (int sampleSize) {
		long neutral = 0;
		for (int i = 1; i <= sampleSize; i++) {
			neutral += Math.pow (Byte.MAX_VALUE - Byte.MIN_VALUE, i) / 2;
		}
		return neutral;
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
	public byte [] framesToByteArray (Sound [] channels, int sampleSize, boolean bigEndian, boolean pcmSigned) {
		int length = channels.length * sampleSize * channels [0].getSamples ().length;
		byte [] data = new byte [length];
		// this.log(new LogEvent (LogLevel.PARANOIAC, "SampleSize : " + sampleSize + ", channelsLength : " + channels.length));

		double value = 0;
		double dividedValue = 0;
		byte byteValueSigned = 0;
		long neutral = (pcmSigned ? this.getNeutral (sampleSize) : 0);
		for (int i = 0; i < data.length; i++) {
			int numByte = i % sampleSize;
			int currentChannel = (i / sampleSize) % channels.length;
			int currentFrame = i / (sampleSize * channels.length);
			if (numByte == 0 && channels [currentChannel].getSamples ().length > currentFrame) {
				value = channels [currentChannel].getSamples () [currentFrame] + neutral;
				// this.log(new LogEvent (LogLevel.PARANOIAC, "Sample #" + currentFrame + ", channel : " + currentChannel + ", value : " + String.format("%.0f", value)));
			}
			dividedValue = value / 256;
			byteValueSigned = (byte) (value + (pcmSigned ? Byte.MIN_VALUE : 0));
			// this.log(new LogEvent (LogLevel.PARANOIAC, "Sample #" + currentFrame + ", channel : " + currentChannel + ", numByte : " + numByte + ", byteValue : " + byteValueSigned));

			data [i + (!bigEndian ? 0 : (sampleSize - 2 * numByte) - 1)] = byteValueSigned;
			value = dividedValue;
		}
		return data;
	}

}