package org.toilelibre.soundtransform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.soundtransform.objects.Sound;
import org.toilelibre.soundtransform.observer.LogAware;
import org.toilelibre.soundtransform.observer.LogEvent;
import org.toilelibre.soundtransform.observer.TransformObserver;
import org.toilelibre.soundtransform.observer.LogEvent.LogLevel;
import org.toilelibre.soundtransform.transforms.SoundTransformation;

public class TransformSound implements LogAware {

	TransformObserver []	observers	= new TransformObserver [0];

	public TransformSound (TransformObserver... observers) {
		this.setObservers (observers);
	}

	public TransformSound () {
	}

	public AudioInputStream transformAudioStream (AudioInputStream ais, SoundTransformation... sts) throws IOException {
		Sound [] input = this.fromInputStream (ais);
		Sound [] output = Arrays.copyOf (input, input.length);
		int transformNumber = 0;
		for (SoundTransformation st : sts) {
			for (int i = 0; i < input.length; i++) {
				this.notifyAll ("Transform " + (transformNumber + 1) + "/" + sts.length + " (" + st.getClass ().getSimpleName () + "), channel " + (i + 1) + "/" + input.length);
				if (st instanceof LogAware) {
					((LogAware) st).setObservers (this.observers);
				}
				output [i] = st.transform (output [i]);
			}
			transformNumber++;
		}
		if (sts.length == 0) {
			output = input;
		}
		this.notifyAll ("Transforms done");
		return this.toStream (output, ais.getFormat ());

	}

	private void notifyAll (String s) {
		this.log (new LogEvent (LogLevel.INFO, s));
	}

	@Override
	public void log (LogEvent event) {
		for (TransformObserver to : this.observers) {
			to.notify (event);
		}

	}

	private long getNeutral (int sampleSize) {
		long neutral = 0;
		for (int i = 1; i <= sampleSize; i++) {
			neutral += Math.pow (Byte.MAX_VALUE - Byte.MIN_VALUE, i) / 2;
		}
		return neutral;
	}

	private Sound [] fromInputStream (InputStream ais, int channels, long frameLength, int sampleSize, double sampleRate, boolean bigEndian, boolean pcmSigned) throws IOException {
		this.notifyAll ("Converting input into java object");
		Sound [] ret = new Sound [channels];
		long neutral = (pcmSigned ? this.getNeutral (sampleSize) : 0);
		for (int channel = 0; channel < channels; channel++) {
			ret [channel] = new Sound (new long [(int) frameLength], sampleSize, (int) sampleRate);
		}
		for (int position = 0; position < frameLength; position++) {
			byte [] frame = new byte [sampleSize * channels];
			ais.read (frame);
			this.byteArrayToFrame (frame, ret, position, bigEndian, pcmSigned, neutral);
		}
		this.notifyAll ("Convert done");
		return ret;
	}

	private Sound [] fromInputStream (AudioInputStream ais) throws IOException {
		int channels = ais.getFormat ().getChannels ();
		long frameLength = ais.getFrameLength ();
		int sampleSize = ais.getFormat ().getFrameSize () / channels;
		double sampleRate = ais.getFormat ().getSampleRate ();
		boolean bigEndian = ais.getFormat ().isBigEndian ();
		boolean pcmSigned = ais.getFormat ().getEncoding () == Encoding.PCM_SIGNED;

		return this.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	private void byteArrayToFrame (byte [] frame, Sound [] sound, int position, boolean bigEndian, boolean pcmSigned, long neutral) {
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

	protected Sound [] byteArrayToFrames (byte [] byteArray, int channels, long frameLength, int sampleSize, double sampleRate, boolean bigEndian, boolean pcmSigned) throws IOException {
		this.notifyAll ("[Test] byteArray -> ByteArrayInputStream");

		ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
		return this.fromInputStream (bais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	protected byte [] framesToByteArray (Sound [] channels, int sampleSize, boolean bigEndian, boolean pcmSigned) {
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
			if (numByte == 0) {
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

	private AudioInputStream toStream (Sound [] channels, AudioFormat audioFormat) {

		int length = audioFormat.getFrameSize () * channels [0].getSamples ().length;
		byte [] data = this.framesToByteArray (channels, audioFormat.getFrameSize () / channels.length, audioFormat.isBigEndian (), audioFormat.getEncoding () == Encoding.PCM_SIGNED);
		this.notifyAll ("Creating output file");
		// now save the file
		ByteArrayInputStream bais = new ByteArrayInputStream (data);
		return new AudioInputStream (bais, audioFormat, length / audioFormat.getFrameSize ());
	}

	public void transformFile (File fOrigin, File fDest, SoundTransformation... sts) throws UnsupportedAudioFileException, IOException {
		File file = fOrigin;
		AudioInputStream ais1 = AudioFileHelper.getAudioInputStream (file);
		this.notifyAll ("input : " + ais1.getFormat ().toString ());
		AudioInputStream ais2 = ais1;
		ais2 = this.transformAudioStream (ais1, sts);
		AudioSystem.write (ais2, AudioFileFormat.Type.WAVE, fDest);
		this.notifyAll ("Wrote output");
		this.notifyAll ("output : " + ais2.getFormat ().toString ());
	}

	@Override
	public void setObservers (TransformObserver [] observers2) {
		this.observers = observers2;
		for (TransformObserver observer : observers2) {
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
	}
}
